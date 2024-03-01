package com.netease.cloud.extension.transform;

import com.netease.cloud.extension.annotations.OnClassLoad;
import com.netease.cloud.extension.halder.ExtTransformAnnotation;
import org.hotswap.agent.annotation.LoadEvent;
import org.hotswap.agent.annotation.handler.PluginClassFileTransformer;
import org.hotswap.agent.javassist.*;
import org.hotswap.agent.logging.AgentLogger;
import org.hotswap.agent.util.AppClassLoaderExecutor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.InvocationTargetException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月01日
 * @Version: 1.0
 */
public class HotSwapExtMethodTransformer implements ClassFileTransformer {

    protected static AgentLogger LOGGER = AgentLogger.getLogger(PluginClassFileTransformer.class);

    private final ExtTransformAnnotation<OnClassLoad> extTransformAnnotation;

    private final OnClassLoad onClassLoadAnnotation;

    public HotSwapExtMethodTransformer(ExtTransformAnnotation<OnClassLoad> extTransformAnnotation) {
        this.extTransformAnnotation = extTransformAnnotation;
        this.onClassLoadAnnotation = extTransformAnnotation.getAnnotation();
    }

    /**
     * Transformation callback as registered in initMethod:
     * hotSwapExtTransformer.registerTransformer(). Resolve method parameters to
     * actual values, provide convenience parameters of javassist to streamline
     * the transformation.
     */
    @Override
    public byte[] transform(ClassLoader classLoader, String className, Class<?> redefiningClass,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        LOGGER.info("Transforming.... '{}' using: '{}'", className, extTransformAnnotation);

        // default result
        byte[] result = classfileBuffer;

        // we may need to crate CtClass on behalf of the client and close it after invocation.
        CtClass ctClass = null;

        List<Object> args = new ArrayList<>();
        for (Class<?> type : extTransformAnnotation.getMethod().getParameterTypes()) {
            if (type.isAssignableFrom(ClassLoader.class)) {
                args.add(classLoader);
            } else if (type.isAssignableFrom(String.class)) {
                args.add(className);
            } else if (type.isAssignableFrom(Class.class)) {
                args.add(redefiningClass);
            } else if (type.isAssignableFrom(ProtectionDomain.class)) {
                args.add(protectionDomain);
            } else if (type.isAssignableFrom(byte[].class)) {
                args.add(classfileBuffer);
            } else if (type.isAssignableFrom(ClassPool.class)) {
                ClassPool classPool = new ClassPool();
                classPool.appendSystemPath();
                LOGGER.trace("Adding loader classpath " + classLoader);
                classPool.appendClassPath(new LoaderClassPath(classLoader));
                args.add(classPool);
            } else if (type.isAssignableFrom(CtClass.class)) {
                try {
                    ctClass = createCtClass(classfileBuffer, classLoader);
                    args.add(ctClass);
                } catch (IOException e) {
                    LOGGER.error("Unable create CtClass for '" + className + "'.", e);
                    return result;
                }
            } else if (type.isAssignableFrom(LoadEvent.class)) {
                args.add(redefiningClass == null ? LoadEvent.DEFINE : LoadEvent.REDEFINE);
            } else if (type.isAssignableFrom(AppClassLoaderExecutor.class)) {
                args.add(new AppClassLoaderExecutor(classLoader, protectionDomain));
            } else {
                LOGGER.error("Unable to call init method on plugin '" + extTransformAnnotation.getExtTransformClass() + "'." + " Method parameter type '" + type + "' is not recognized for @Init annotation.");
                return result;
            }
        }
        try {
            // call method on plugin (or if plugin null -> static method)
            Object resultObject = extTransformAnnotation.getMethod().invoke(extTransformAnnotation.getExtTransform(), args.toArray());

            if (resultObject == null) {
                // Ok, nothing has changed
            } else if (resultObject instanceof byte[]) {
                result = (byte[]) resultObject;
            } else if (resultObject instanceof CtClass) {
                result = ((CtClass) resultObject).toBytecode();

                // detach on behalf of the clinet - only if this is another
                // instance than we created (it is closed elsewhere)
                if (resultObject != ctClass) {
                    ((CtClass) resultObject).detach();
                }
            } else {
                LOGGER.error("Unknown result of @OnClassLoadEvent method '" + result.getClass().getName() + "'.");
            }

            // close CtClass if created from here
            if (ctClass != null) {
                // if result not set from the method, use class
                if (resultObject == null) {
                    result = ctClass.toBytecode();
                }
                ctClass.detach();
            }

        } catch (IllegalAccessException e) {
            LOGGER.error("IllegalAccessException in transform method on extension transform '{}' class '{}' of classLoader '{}'",
                    e, extTransformAnnotation.getExtTransformClass(), className,
                    classLoader != null ? classLoader.getClass().getName() : "");
        } catch (InvocationTargetException e) {
            LOGGER.error("InvocationTargetException in transform method on extension transform '{}' class '{}' of classLoader '{}'",
                    e, extTransformAnnotation.getExtTransformClass(), className,
                    classLoader != null ? classLoader.getClass().getName() : "");
        } catch (CannotCompileException e) {
            LOGGER.error("Cannot compile class after manipulation on extension transform '{}' class '{}' of classLoader '{}'",
                    e, extTransformAnnotation.getExtTransformClass(), className,
                    classLoader != null ? classLoader.getClass().getName() : "");
        } catch (IOException e) {
            LOGGER.error("IOException in transform method on extension transform '{}' class '{}' of classLoader '{}'",
                    e, extTransformAnnotation.getExtTransformClass(), className,
                    classLoader != null ? classLoader.getClass().getName() : "");
        }

        return result;
    }

    /**
     * Creats javaassist CtClass for bytecode manipulation. Add default
     * classloader.
     *
     * @param bytes new class definition
     * @param classLoader loader
     * @return created class
     * @throws NotFoundException
     */
    private static CtClass createCtClass(byte[] bytes, ClassLoader classLoader) throws IOException {
        ClassPool cp = new ClassPool();
        cp.appendSystemPath();
        cp.appendClassPath(new LoaderClassPath(classLoader));

        return cp.makeClass(new ByteArrayInputStream(bytes));
    }

}
