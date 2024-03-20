package com.netease.cloud.core.utils;

import com.netease.cloud.core.handler.HotSwapClassFileHandler;
import org.hotswap.agent.javassist.CannotCompileException;
import org.hotswap.agent.javassist.ClassPool;
import org.hotswap.agent.javassist.CtClass;
import org.hotswap.agent.logging.AgentLogger;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月20日
 * @Version: 1.0
 */
public class ClassHelper {

    private static final AgentLogger LOGGER = AgentLogger.getLogger(HotSwapClassFileHandler.class);

    public static Class<?> loadClass(ClassLoader appClassLoader, String clazzName, byte[] classDefinition) {
        Class<?> clazz = doLoadClass(appClassLoader, clazzName);
        if (clazz != null) {
            return clazz;
        }
        ClassPool pool = new ClassPool() {
            @Override
            public ClassLoader getClassLoader() {
                return appClassLoader;
            }
        };
        try {
            CtClass ctClass = pool.makeClass(new ByteArrayInputStream(classDefinition));
            clazz = doLoadClass(appClassLoader, ctClass.getName());
            if (clazz != null) {
                return clazz;
            }
            return ctClass.toClass();
        } catch (IOException | CannotCompileException e) {
            LOGGER.error("make new class failed, {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static Class<?> doLoadClass(ClassLoader appClassLoader, String clazzName) {
        try {
            if (clazzName == null || clazzName.isEmpty()) {
                return null;
            }
            String realClassName = clazzName.replaceAll("/", ".");
            return appClassLoader.loadClass(realClassName);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            // ignore
        }
        return null;
    }

}
