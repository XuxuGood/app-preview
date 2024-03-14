package com.netease.cloud.core.handler;

import com.google.gson.reflect.TypeToken;
import com.netease.cloud.HotSwapEntrance;
import com.netease.cloud.core.config.HotSwapConfiguration;
import com.netease.cloud.core.model.BatchModifiedClassRequest;
import com.netease.cloud.core.model.BatchModifiedResourceRequest;
import com.netease.cloud.core.model.HotSwapResponse;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.hotswap.agent.extension.AutoChoose;
import org.hotswap.agent.extension.manager.AllExtensionsManager;
import org.hotswap.agent.logging.AgentLogger;
import org.hotswap.agent.util.JsonUtils;
import org.hotswap.agent.util.spring.util.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static com.netease.cloud.core.handler.HotSwapClassFileHandler.TARGET_CLASS_PATH;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月04日
 * @Version: 1.0
 */
public class HotSwapResourceFileHandler implements Handler<RoutingContext> {

    private static final AgentLogger LOGGER = AgentLogger.getLogger(HotSwapClassFileHandler.class);

    private final AutoChoose autoChoose;
    private final String watchResourcesPath;

    public HotSwapResourceFileHandler() {
        autoChoose = new AutoChoose();
        watchResourcesPath = HotSwapConfiguration.getInstance().getProperties().getProperty("watchResources");
    }

    @Override
    public void handle(RoutingContext routingContext) {
        routingContext.request().bodyHandler(requestBody -> {
            String bodyString = requestBody.toString();

            Type listType = TypeToken.getParameterized(List.class, BatchModifiedResourceRequest.class).getType();
            List<BatchModifiedResourceRequest> requestResourceList = JsonUtils.parse(bodyString, listType);

            LOGGER.debug("hotswap resource request params: {}, to pojo: {}", bodyString, requestResourceList);

            // 获取classloader
            ClassLoader classLoader = AllExtensionsManager.getInstance().getClassLoader();

            String resourcePath;
            URL classPathResource = classLoader.getResource("");
            String rootClassPath = Objects.requireNonNull(classPathResource).getPath();

            for (BatchModifiedResourceRequest requestResource : requestResourceList) {
                if (!StringUtils.isEmpty(watchResourcesPath)) {
                    // 支持
                    resourcePath = Paths.get(watchResourcesPath, requestResource.getRelativePath()).toString();
                } else {
                    // 解压jar包形式热更新
                    if (rootClassPath.endsWith(TARGET_CLASS_PATH)) {
                        resourcePath = Paths.get(rootClassPath, requestResource.getRelativePath()).toString();
                    } else {
                        resourcePath = Paths.get(rootClassPath, TARGET_CLASS_PATH, requestResource.getRelativePath()).toString();
                    }
                }

                byte[] resourceBytes = requestResource.getContent().getBytes();

                // 前置处理
                autoChoose.preHandle(classLoader, resourcePath, resourceBytes);

                // 将content内容写进path文件中
                try {
                    Path destinationPath = Paths.get(resourcePath);
                    Files.createDirectories(destinationPath.getParent());

                    try (FileOutputStream fos = new FileOutputStream(resourcePath)) {
                        // 将content内容写入到文件中
                        fos.write(resourceBytes);
                        fos.flush();
                    }
                } catch (IOException e) {
                    LOGGER.error("Exception writing to file：" + e.getMessage(), e);
                    HotSwapResponse errorResponse = HotSwapResponse.of("Exception writing to file", 400, e.getMessage());
                    HttpServerResponse response = routingContext.response();
                    response.end(JsonObject.mapFrom(errorResponse).toBuffer());
                    return;
                }

                // 后置处理
                autoChoose.afterHandle(classLoader, null, resourcePath, resourceBytes);
            }

            HotSwapResponse successResponse = HotSwapResponse.success("resource updated successfully");
            HttpServerResponse response = routingContext.response();
            response.end(JsonObject.mapFrom(successResponse).toBuffer());
        });
    }

}
