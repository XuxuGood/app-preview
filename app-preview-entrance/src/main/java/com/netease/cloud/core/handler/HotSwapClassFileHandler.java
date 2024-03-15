package com.netease.cloud.core.handler;

import com.google.gson.reflect.TypeToken;
import com.netease.cloud.HotSwapEntrance;
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
import org.hotswap.agent.javassist.*;
import org.hotswap.agent.logging.AgentLogger;
import org.hotswap.agent.util.JsonUtils;
import org.hotswap.agent.util.spring.util.StringUtils;
import org.hotswap.agent.watch.nio.AbstractNIO2Watcher;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
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

    public static final String TARGET_CLASS_PATH = "BOOT-INF/classes/";

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
                if (!StringUtils.isEmpty(extraClasspath)) {
                    // 支持jar包形式热更新（外挂classpath）
                    hotswapByExtraClasspath(requestList);
                } else {
                    // 解压jar包形式热更新
                    hotswapByBootInf(requestList);
                }
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

    private void hotswapByBootInf(List<BatchModifiedClassRequest> requestList) throws IOException, CannotCompileException {
        // 获取classloader
        ClassLoader classLoader = AllExtensionsManager.getInstance().getClassLoader();

        Map<Class<?>, byte[]> reloadMap = new LinkedHashMap<>();
        Map<Class<?>, byte[]> afterHandlerMap = new LinkedHashMap<>();

        handleHotswapClass(reloadMap, afterHandlerMap, requestList, classLoader);

        // 热更新
        PluginManager.getInstance().hotswap(reloadMap);

        // 热更新后置处理
        afterHandlerMap.forEach((aClass, bytes) -> autoChoose.afterHandle(classLoader, aClass, aClass.getName(), bytes));
    }

    private void hotswapByExtraClasspath(List<BatchModifiedClassRequest> requestList) throws IOException, CannotCompileException {
        // 获取classloader
        ClassLoader classLoader = AllExtensionsManager.getInstance().getClassLoader();

        for (BatchModifiedClassRequest classRequest : requestList) {
            String className = classRequest.getClassName();
            byte[] classBytes = classRequest.getBytes();

            // 热更新前置处理
            autoChoose.preHandle(classLoader, className, classBytes);

            Class<?> clazz;
            try {
                clazz = classLoader.loadClass(className);
                // 能加载到的Class，则放到热更新根目录
                int lastDotIndex = className.lastIndexOf(".");

                String classDestinationPath = Paths.get(extraClasspath, className.substring(lastDotIndex + 1) + ".class").toString();

                Path destinationPath = Paths.get(classDestinationPath);
                Files.createDirectories(destinationPath.getParent());

                // 写入class文件
                Files.copy(new ByteArrayInputStream(classBytes), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (ClassNotFoundException e) {
                // 不存在的Class丢到实际包目录下，类加载会自动去extraClasspath下找
                String classDestinationPath = Paths.get(extraClasspath, className.replace('.', '/') + ".class").toString();
                Path destinationPath = Paths.get(classDestinationPath);

                if (!Files.exists(destinationPath.getParent())) {
                    Files.createDirectories(destinationPath.getParent());
                    // 注册热更新目录监听
                    watcher.addDirectory(Paths.get(extraClasspath));
                }

                // 写入class文件
                Files.copy(new ByteArrayInputStream(classBytes), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                ClassPool classPool = new ClassPool() {
                    @Override
                    public ClassLoader getClassLoader() {
                        return classLoader;
                    }
                };
                classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
                classPool.appendClassPath(new LoaderClassPath(classLoader));
                CtClass newCtClass = classPool.makeClass(new ByteArrayInputStream(classBytes));
                clazz = newCtClass.toClass();
            }

            // 热更新后置处理
            autoChoose.afterHandle(classLoader, clazz, clazz.getName(), classBytes);
        }
    }

    private void handleHotswapClass(Map<Class<?>, byte[]> reloadMap,
                                    Map<Class<?>, byte[]> afterHandlerMap,
                                    List<BatchModifiedClassRequest> requestList,
                                    ClassLoader classLoader) throws IOException, CannotCompileException {
        String classDestinationPath;
        URL classPathResource = classLoader.getResource("");
        String rootClassPath = Objects.requireNonNull(classPathResource).getPath();

        for (BatchModifiedClassRequest classRequest : requestList) {
            String className = classRequest.getClassName();
            byte[] classBytes = classRequest.getBytes();

            // 热更新前置处理
            autoChoose.preHandle(classLoader, className, classBytes);

            // 是否已经加载过
            boolean isLoaded;

            try {
                Class<?> clazz = classLoader.loadClass(className);
                reloadMap.put(clazz, classBytes);
                afterHandlerMap.put(clazz, classBytes);
                isLoaded = true;
            } catch (ClassNotFoundException e) {
                isLoaded = false;
            }

            // new class
            if (!isLoaded) {
                ClassPool classPool = new ClassPool() {
                    @Override
                    public ClassLoader getClassLoader() {
                        return classLoader;
                    }
                };
                classPool.appendSystemPath();
                classPool.appendClassPath(new LoaderClassPath(classLoader));

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(classBytes);

                if (rootClassPath.endsWith(TARGET_CLASS_PATH)) {
                    classDestinationPath = Paths.get(rootClassPath, className.replace('.', '/') + ".class").toString();
                } else {
                    classDestinationPath = Paths.get(rootClassPath, TARGET_CLASS_PATH, className.replace('.', '/') + ".class").toString();
                }

                Path destinationPath = Paths.get(classDestinationPath);
                Files.createDirectories(destinationPath.getParent());

                // 写入class文件
                Files.copy(byteArrayInputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);

                // 重置输入流的位置
                byteArrayInputStream.reset();

                CtClass newCtClass = classPool.makeClass(byteArrayInputStream);
                Class<?> newClass = newCtClass.toClass();
                reloadMap.put(newClass, classBytes);
                afterHandlerMap.put(newClass, classBytes);
            }
        }
    }

}
