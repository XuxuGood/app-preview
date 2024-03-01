package com.netease.cloud.extension.transform;

import org.hotswap.agent.config.PluginManager;
import org.hotswap.agent.logging.AgentLogger;
import org.hotswap.agent.util.HotswapTransformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 热更新扩展动态字节码增强
 *
 * @Author xiaoxuxuy
 * @Date 2024年03月01日
 * @Version: 1.0
 */
public class HotSwapExtTransformer implements ClassFileTransformer {

    private static final AgentLogger LOGGER = AgentLogger.getLogger(HotswapTransformer.class);

    private static class RegisteredTransformersRecord {
        Pattern pattern;
        List<ClassFileTransformer> transformerList = new LinkedList<>();
    }

    protected Map<ClassLoader, Boolean> seenClassLoaders = new WeakHashMap<>();

    /**
     * Exclude these classLoaders from initialization (system classloaders). Note that
     */
    private static final Set<String> skippedClassLoaders = new HashSet<>(Arrays.asList(
            "jdk.internal.reflect.DelegatingClassLoader",
            "sun.reflect.DelegatingClassLoader"
    ));

    // TODO : check if felix class loaders could be skipped
    private static final Set<String> excludedClassLoaders = new HashSet<>(Arrays.asList(
            "org.apache.felix.framework.BundleWiringImpl$BundleClassLoader", // delegating ClassLoader in GlassFish
            "org.apache.felix.framework.BundleWiringImpl$BundleClassLoaderJava5" // delegating ClassLoader in_GlassFish
    ));

    private List<Pattern> includedClassLoaderPatterns;
    private List<Pattern> excludedClassLoaderPatterns;

    protected Map<String, HotSwapExtTransformer.RegisteredTransformersRecord> otherTransformers = new LinkedHashMap<>();

    @Override
    public byte[] transform(ClassLoader classLoader, String className, Class<?> redefiningClass,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        // Skip delegating classloaders used for reflection
        String classLoaderClassName = classLoader != null ? classLoader.getClass().getName() : null;
        if (skippedClassLoaders.contains(classLoaderClassName)) {
            return classfileBuffer;
        }

        LOGGER.trace("Transform on class '{}' @{} redefiningClass '{}'.", className, classLoader, redefiningClass);

        List<ClassFileTransformer> toApply = new ArrayList<>();
        List<HotSwapExtMethodTransformer> extMethodTransformers = new ArrayList<>();
        try {
            // call transform method of defining transformers
            for (HotSwapExtTransformer.RegisteredTransformersRecord transformerRecord : new ArrayList<>(otherTransformers.values())) {
                if ((className != null && transformerRecord.pattern.matcher(className).matches()) ||
                        (redefiningClass != null && transformerRecord.pattern.matcher(redefiningClass.getName()).matches())) {
                    for (ClassFileTransformer transformer : new ArrayList<>(transformerRecord.transformerList)) {
                        if (transformer instanceof HotSwapExtMethodTransformer) {
                            HotSwapExtMethodTransformer extMethodTransformer = (HotSwapExtMethodTransformer) transformer;
                            extMethodTransformers.add(extMethodTransformer);
                        } else {
                            toApply.add(transformer);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.error("Error transforming class '" + className + "'.", t);
        }

        if(toApply.isEmpty() && extMethodTransformers.isEmpty()) {
            LOGGER.trace("No transformers define for {} ", className);
            return classfileBuffer;
        }

        try {
            byte[] result = classfileBuffer;

            for(ClassFileTransformer transformer: extMethodTransformers) {
                LOGGER.trace("Transforming class '" + className + "' with transformer '" + transformer + "' " + "@ClassLoader" + classLoader + ".");
                result = transformer.transform(classLoader, className, redefiningClass, protectionDomain, result);
            }

            for(ClassFileTransformer transformer: toApply) {
                LOGGER.trace("Transforming class '" + className + "' with transformer '" + transformer + "' " + "@ClassLoader" + classLoader + ".");
                result = transformer.transform(classLoader, className, redefiningClass, protectionDomain, result);
            }

            return result;
        } catch (Throwable t) {
            LOGGER.error("Error transforming class '" + className + "'.", t);
        }

        return classfileBuffer;
    }

    public void registerTransformer(String classNameRegexp, HotSwapExtMethodTransformer transformer) {
        LOGGER.debug("Registering transformer for class regexp '{}'.", classNameRegexp);

        String normalizeRegexp = normalizeTypeRegexp(classNameRegexp);

        HotSwapExtTransformer.RegisteredTransformersRecord transformerRecord = otherTransformers.get(normalizeRegexp);
        if (transformerRecord == null) {
            transformerRecord = new HotSwapExtTransformer.RegisteredTransformersRecord();
            transformerRecord.pattern = Pattern.compile(normalizeRegexp);
            otherTransformers.put(normalizeRegexp, transformerRecord);
        }

        if (!transformerRecord.transformerList.contains(transformer)) {
            transformerRecord.transformerList.add(transformer);
        }
    }

    /**
     * Free all classloader references and close any associated plugin instance.
     * Typical use is after webapp undeploy.
     *
     * @param appClassLoader clasloade to free
     */
    public static void callCloseClassLoader(ClassLoader appClassLoader) {
        PluginManager.getInstance().closeClassLoader(appClassLoader);
    }

    /**
     * Transform type to ^regexp$ form - match only whole pattern.
     *
     * @param registeredType type
     * @return
     */
    private String normalizeTypeRegexp(String registeredType) {
        String regexp = registeredType;
        if (!registeredType.startsWith("^")) {
            regexp = "^" + regexp;
        }
        if (!registeredType.endsWith("$")) {
            regexp = regexp + "$";
        }

        return regexp;
    }

}
