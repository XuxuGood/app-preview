package com.netease.cloud.extension.transform;

import com.netease.cloud.extension.annotations.ClassTransform;
import com.netease.cloud.extension.halder.AnnotationProcessor;
import com.netease.cloud.extension.util.ClassPathAnnotationScanner;
import org.hotswap.agent.config.PluginManager;
import org.hotswap.agent.logging.AgentLogger;
import org.hotswap.agent.util.scanner.ClassPathScanner;

import java.lang.instrument.Instrumentation;
import java.util.List;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月01日
 * @Version: 1.0
 */
public class HotSwapExtManager {

    public static final String EXT_TRANSFORM_PACKAGE = "com.netease.cloud.extension.transform";

    private static final AgentLogger LOGGER = AgentLogger.getLogger(PluginManager.class);

    private static HotSwapExtManager INSTANCE = new HotSwapExtManager();

    public static HotSwapExtManager getInstance() {
        return INSTANCE;
    }

    protected HotSwapExtTransformer hotSwapExtTransformer;

    private ClassPathAnnotationScanner annotationScanner;

    protected AnnotationProcessor annotationProcessor;

    private HotSwapExtManager() {
        hotSwapExtTransformer = new HotSwapExtTransformer();
        annotationProcessor = new AnnotationProcessor(this);
        annotationScanner = new ClassPathAnnotationScanner(ClassTransform.class.getName(), new ClassPathScanner());
    }

    public void init(Instrumentation instrumentation) {
        scanExtTransforms(getClass().getClassLoader());

        LOGGER.debug("Registering extension transformer ");
        instrumentation.addTransformer(hotSwapExtTransformer);
    }

    private void scanExtTransforms(ClassLoader classLoader) {
        String extTransformPath = EXT_TRANSFORM_PACKAGE.replace(".", "/");
        ClassLoader agentClassLoader = getClass().getClassLoader();
        try {
            List<String> discoveredExtTransforms = annotationScanner.scanExtTransforms(classLoader, extTransformPath);
            for (String discoveredExtTransform : discoveredExtTransforms) {
                Class<?> extTransformClass = Class.forName(discoveredExtTransform, true, agentClassLoader);
                ClassTransform classTransformAnnotation = extTransformClass.getAnnotation(ClassTransform.class);

                if (classTransformAnnotation == null) {
                    LOGGER.error("Scanner discovered extension transform class {} which does not contain @ClassTransform annotation.", extTransformClass);
                    continue;
                }

                if (annotationProcessor.processAnnotations(extTransformClass, extTransformClass)) {
                    LOGGER.debug("extension transform registered {}.", extTransformClass);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in extension transform initial processing for extension transform package '{}'", e, extTransformPath);
        }
    }

    public HotSwapExtTransformer getHotswapExtTransformer() {
        return hotSwapExtTransformer;
    }

}
