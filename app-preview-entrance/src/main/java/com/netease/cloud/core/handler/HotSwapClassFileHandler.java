package com.netease.cloud.core.handler;

import com.google.gson.reflect.TypeToken;
import com.netease.cloud.core.config.HotSwapConfiguration;
import com.netease.cloud.core.model.BatchModifiedClassRequest;
import com.netease.cloud.core.model.HotSwapResponse;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.hotswap.agent.config.PluginManager;
import org.hotswap.agent.extension.AutoChoose;
import org.hotswap.agent.extension.manager.AllExtensionsManager;
import org.hotswap.agent.logging.AgentLogger;
import org.hotswap.agent.util.JsonUtils;
import org.hotswap.agent.watch.nio.AbstractNIO2Watcher;
import com.netease.cloud.core.utils.ClassHelper;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;


/**
 * @Author xiaoxuxuy
 * @Date 2024年03月04日
 * @Version: 1.0
 */
public class HotSwapClassFileHandler implements Handler<RoutingContext> {

    private static final AgentLogger LOGGER = AgentLogger.getLogger(HotSwapClassFileHandler.class);

    private final AutoChoose autoChoose;

    private final String extraClasspath;

    private final AbstractNIO2Watcher watcher;

    public HotSwapClassFileHandler() {
        autoChoose = new AutoChoose();
        watcher = (AbstractNIO2Watcher) PluginManager.getInstance().getWatcher();
        extraClasspath = HotSwapConfiguration.getInstance().getProperties().getProperty("extraClasspath");
    }

    @Override
    public void handle(RoutingContext routingContext) {
        routingContext.request().bodyHandler(body -> {
            String bodyString = body.toString();

            Type listType = TypeToken.getParameterized(List.class, BatchModifiedClassRequest.class).getType();
            List<BatchModifiedClassRequest> requestList = JsonUtils.parse(bodyString, listType);

            LOGGER.debug("hotswap class request params: {}, to pojo: {}", bodyString, requestList);

            try {
                hotswap(requestList);
            } catch (Exception e) {
                LOGGER.error("hotswap class file error", e);
                HotSwapResponse success = HotSwapResponse.of("hotswap class file error", 400, e.getMessage());
                HttpServerResponse response = routingContext.response();
                response.end(JsonObject.mapFrom(success).toBuffer());
                return;
            }

            HotSwapResponse success = HotSwapResponse.success("success, updates(include inner classes)=" + requestList.size());
            HttpServerResponse response = routingContext.response();
            response.end(JsonObject.mapFrom(success).toBuffer());
        });
    }

    private void hotswap(List<BatchModifiedClassRequest> requestList) throws IOException {
        // 获取classloader
        ClassLoader classLoader = AllExtensionsManager.getInstance().getClassLoader();

        for (BatchModifiedClassRequest classRequest : requestList) {
            String className = classRequest.getClassName();
            byte[] classBytes = classRequest.getBytes();

            // 热更新前置处理
            autoChoose.preHandle(classLoader, className, classBytes);

            Class<?> clazz = ClassHelper.loadClass(classLoader, className, classBytes);

            String classDestinationPath = Paths.get(extraClasspath, className.replace('.', '/') + ".class").toString();
            Path destinationPath = Paths.get(classDestinationPath);

            if (!Files.exists(destinationPath.getParent())) {
                Files.createDirectories(destinationPath.getParent());
                // 注册热更新目录监听
                watcher.addDirectory(Paths.get(extraClasspath));
            }

            // 写入class文件
            Files.copy(new ByteArrayInputStream(classBytes), destinationPath, StandardCopyOption.REPLACE_EXISTING);

            // 热更新后置处理
            autoChoose.afterHandle(classLoader, clazz, classDestinationPath, classBytes);
        }
    }

}
