package com.netease.cloud.extension.halder;

import org.hotswap.agent.annotation.handler.PluginAnnotation;

import java.lang.annotation.Annotation;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月01日
 * @Version: 1.0
 */
public interface ExtTransformHandler<T extends Annotation> {

    /**
     * Initialization for field annotations.
     *
     * @param extTransformAnnotation annotation values
     * @return true if initialized.
     */
    boolean initField(ExtTransformAnnotation<T> extTransformAnnotation);

    /**
     * Initialization for method annotations.
     *
     * @param extTransformAnnotation annotation values
     * @return true if initialized.
     */
    boolean initMethod(ExtTransformAnnotation<T> extTransformAnnotation);


}
