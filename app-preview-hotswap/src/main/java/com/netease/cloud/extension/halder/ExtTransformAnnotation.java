package com.netease.cloud.extension.halder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月01日
 * @Version: 1.0
 */
public class ExtTransformAnnotation<T extends Annotation> {

    // the main extension transform class
    Class<?> extTransformClass;

    // target transform object
    Object extTransform;

    // the annotation to process
    T annotation;

    // annotation is on a method (and field property is empty)
    Method method;

    public ExtTransformAnnotation(Class<?> extTransformClass, Object extTransform, T annotation, Method method) {
        this.extTransformClass = extTransformClass;
        this.extTransform = extTransform;
        this.annotation = annotation;
        this.method = method;
    }

    public Class<?> getExtTransformClass() {
        return extTransformClass;
    }

    public Object getExtTransform() {
        return extTransform;
    }

    public T getAnnotation() {
        return annotation;
    }

    public Method getMethod() {
        return method;
    }

}
