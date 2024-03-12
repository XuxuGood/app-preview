package com.netease.cloud.core.handler;

import com.google.gson.reflect.TypeToken;
import com.netease.cloud.core.model.BatchModifiedClassRequest;
import com.netease.cloud.core.model.HotSwapResponse;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.ibatis.annotations.Mapper;
import org.hotswap.agent.config.PluginManager;
import org.hotswap.agent.extension.AutoChoose;
import org.hotswap.agent.extension.manager.AllExtensionsManager;
import org.hotswap.agent.javassist.*;
import org.hotswap.agent.logging.AgentLogger;
import org.hotswap.agent.util.JsonUtils;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Type;
import java.util.*;

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

            LOGGER.debug("hotswap class request params: {}, to pojo: {}", bodyString, requestList);

            // 获取classloader
            ClassLoader classLoader = AllExtensionsManager.getInstance().getClassLoader();

            Map<Class<?>, byte[]> reloadMap = new LinkedHashMap<>();
            Map<Class<?>, byte[]> afterHandlerMap = new LinkedHashMap<>();

            try {
                handleHotswapClass(reloadMap, afterHandlerMap, requestList, classLoader);
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
            afterHandlerMap.forEach((aClass, bytes) -> autoChoose.afterHandle(classLoader, aClass, aClass.getName(), bytes));

            HotSwapResponse success = HotSwapResponse.success("success, updates(include inner classes)=" + requestList.size());

            HttpServerResponse response = routingContext.response();
            response.end(JsonObject.mapFrom(success).toBuffer());
        });
    }

    private void handleHotswapClass(Map<Class<?>, byte[]> reloadMap,
                                    Map<Class<?>, byte[]> afterHandlerMap,
                                    List<BatchModifiedClassRequest> requestList,
                                    ClassLoader classLoader) throws IOException, CannotCompileException {
        for (BatchModifiedClassRequest classRequest : requestList) {
            String className = classRequest.getClassName();
            byte[] classBytes = classRequest.getBytes();

            // 热更新前置处理
            autoChoose.preHandle(classLoader, className, classBytes);

            boolean isLoaded;

            // 如果不是SprintBoot的类加载器，需要额外处理
            if (classLoader.getClass().getName().equals("org.springframework.boot.loader.LaunchedURLClassLoader")) {
                Class<?> clazz;
                try {
                    clazz = classLoader.loadClass(className);
                    reloadMap.put(clazz, classBytes);
                    afterHandlerMap.put(clazz, classBytes);
                    isLoaded = true;
                } catch (ClassNotFoundException e) {
                    isLoaded = false;
                }
            } else {
                Class<?>[] loadedClasses = instrumentation.getAllLoadedClasses();

                // 判断是否已经加载过该类
                isLoaded = Arrays.stream(loadedClasses)
                        .filter(clazz -> clazz.getName().equals(className))
                        .peek(clazz -> {
                            reloadMap.put(clazz, classBytes);
                            afterHandlerMap.put(clazz, classBytes);
                        })
                        .findFirst()
                        .isPresent();
            }

            if (!isLoaded) {
                // need to recreate class pool on each swap to avoid stale class definition
                ClassPool classPool =  new ClassPool() {
                    @Override
                    public ClassLoader getClassLoader() {
                        return classLoader;
                    }
                };
                classPool.appendSystemPath();
                classPool.appendClassPath(new LoaderClassPath(classLoader));

                CtClass newCtClass = classPool.makeClass(new ByteArrayInputStream(classBytes));
                Class<?> newClass = newCtClass.toClass();
                reloadMap.put(newClass, classBytes);
                afterHandlerMap.put(newClass, classBytes);
            }
        }
    }

}
