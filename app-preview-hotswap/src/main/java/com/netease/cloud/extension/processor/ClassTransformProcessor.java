//package com.netease.cloud.extension.processor;
//
//import com.netease.cloud.extension.annotations.ClassTransform;
//import com.netease.cloud.extension.annotations.OnClassLoad;
//import javassist.ClassPool;
//import javassist.CtClass;
//import javassist.CtMethod;
//import org.hotswap.agent.annotation.Init;
//import org.hotswap.agent.util.HotswapTransformer;
//import org.reflections.Reflections;
//
//import java.io.File;
//import java.io.IOException;
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Method;
//import java.net.URL;
//import java.net.URLDecoder;
//import java.util.ArrayList;
//import java.util.Enumeration;
//import java.util.List;
//import java.util.Set;
//
///**
// * @Author xiaoxuxuy
// * @Date 2024年02月29日
// * @Version: 1.0
// */
//public class ClassTransformProcessor {
//
//    public static void transform(Class<?> clazz) {
//        try {
//            ClassPool classPool = ClassPool.getDefault();
//            CtClass ctClass = classPool.get(clazz.getName());
//
//            for (CtMethod ctMethod : ctClass.getMethods()) {
//                OnClassLoad onClassLoad = (OnClassLoad) ctMethod.getAnnotation(OnClassLoad.class);
//                if (onClassLoad != null) {
//                    String className = onClassLoad.className();
//                    if (className.equals(clazz.getName())) {
//                        ctMethod.invoke(null, ctClass, classPool);
//                    }
//                }
//            }
//
//            ctClass.toClass();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}
