package com.netease.cloud.extension.halder;

import com.netease.cloud.extension.annotations.OnClassLoad;
import com.netease.cloud.extension.halder.impl.OnClassLoadHandler;
import com.netease.cloud.extension.transform.HotSwapExtManager;
import org.hotswap.agent.annotation.OnClassLoadEvent;
import org.hotswap.agent.config.PluginManager;
import org.hotswap.agent.logging.AgentLogger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月01日
 * @Version: 1.0
 */
public class AnnotationProcessor {

    private static AgentLogger LOGGER = AgentLogger.getLogger(AnnotationProcessor.class);

    private final OnClassLoadHandler onClassLoadHandler;

    public AnnotationProcessor(HotSwapExtManager hotSwapExtManager) {
        onClassLoadHandler = new OnClassLoadHandler(hotSwapExtManager);
    }

    public boolean processAnnotations(Class processClass, Class extTransformClass) {
        try {
            for (Method method : processClass.getDeclaredMethods()) {
                if (Modifier.isStatic(method.getModifiers()))
                    if (!processMethodAnnotations(null, method, extTransformClass))
                        return false;
            }
            return true;
        } catch (Throwable e) {
            LOGGER.error("Unable to process extension transform annotations '{}'", e, extTransformClass);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private boolean processMethodAnnotations(Object extTransform, Method method, Class extTransformClass) {
        // for all methods and all handlers
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            if (annotation.annotationType().equals(OnClassLoad.class)) {
                ExtTransformAnnotation<?> extTransformAnnotation = new ExtTransformAnnotation<>(extTransformClass, extTransform, annotation, method);
                if (!onClassLoadHandler.initMethod((ExtTransformAnnotation<OnClassLoad>) extTransformAnnotation)) {
                    return false;
                }
            }
        }
        return true;
    }

}
