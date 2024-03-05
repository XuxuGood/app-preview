package com.netease.cloud.core.handler;

import com.google.gson.reflect.TypeToken;
import com.netease.cloud.core.model.BatchModifiedClassRequest;
import com.netease.cloud.core.model.HotSwapResponse;
import com.netease.cloud.extension.AutoChoose;
import com.netease.cloud.extension.util.JsonUtils;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.hotswap.agent.config.PluginManager;
import org.hotswap.agent.javassist.*;
import org.hotswap.agent.logging.AgentLogger;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月04日
 * @Version: 1.0
 */
public class HotSwapClassFileHandler implements Handler<RoutingContext> {

    private static final AgentLogger LOGGER = AgentLogger.getLogger(HotSwapClassFileHandler.class);

    private final AutoChoose autoChoose;

    private final Instrumentation instrumentation;

    public HotSwapClassFileHandler(Instrumentation instrumentation) {
        autoChoose = new AutoChoose();
        this.instrumentation = instrumentation;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        routingContext.request().bodyHandler(body -> {
            String bodyString = body.toString();

            Type listType = TypeToken.getParameterized(List.class, BatchModifiedClassRequest.class).getType();
            List<BatchModifiedClassRequest> requestList = JsonUtils.parse(bodyString, listType);

            LOGGER.info("hotswap request params: {}, to pojo: {}", bodyString, requestList);

            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

            Map<Class<?>, byte[]> reloadMap = new LinkedHashMap<>();
            Map<Class<?>, byte[]> afterHandlerMap = new LinkedHashMap<>();

            try {
                requestList.forEach(request -> {
                    String className = request.getClassName();
                    byte[] classBytes = request.getBytes();

                    // 热更新前置处理
                    autoChoose.preHandle(contextClassLoader, className, classBytes);

                    // need to recreate class pool on each swap to avoid stale class definition
                    ClassPool classPool = new ClassPool();
                    classPool.appendClassPath(new LoaderClassPath(getClass().getClassLoader()));

                    Class<?>[] loadedClasses = instrumentation.getAllLoadedClasses();

                    // 判断是否已经加载过该类
                    boolean isLoaded = Arrays.stream(loadedClasses)
                            .filter(clazz -> clazz.getName().equals(className))
                            .peek(clazz -> {
                                reloadMap.put(clazz, classBytes);
                                afterHandlerMap.put(clazz, classBytes);
                            })
                            .findFirst()
                            .isPresent();

                    if (!isLoaded) {
                        try {
                            CtClass newCtClass = classPool.makeClass(new ByteArrayInputStream(classBytes));
                            Class<?> newClass = newCtClass.toClass();
                            reloadMap.put(newClass, classBytes);
                            afterHandlerMap.put(newClass, classBytes);
                        } catch (CannotCompileException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            } catch (Exception e) {
                LOGGER.error("hotswap class file error", e);
                HotSwapResponse success = HotSwapResponse.of("hotswap class file error", 400, e.getMessage());
                HttpServerResponse response = routingContext.response();
                response.end(JsonObject.mapFrom(success).toBuffer());
                return;
            }

            // 热更新
            PluginManager.getInstance().hotswap(reloadMap);

            // 热更新后置处理
            afterHandlerMap.forEach((aClass, bytes) -> {
                autoChoose.afterHandle(contextClassLoader, aClass, aClass.getName(), bytes);
            });

            HotSwapResponse success = HotSwapResponse.success("success, updates(include inner classes)=" + requestList.size());

            HttpServerResponse response = routingContext.response();
            response.end(JsonObject.mapFrom(success).toBuffer());
        });
    }

}