package com.netease.cloud.extension.halder.impl;

import com.netease.cloud.extension.annotations.OnClassLoad;
import com.netease.cloud.extension.halder.ExtTransformAnnotation;
import com.netease.cloud.extension.halder.ExtTransformHandler;
import com.netease.cloud.extension.transform.HotSwapExtManager;
import com.netease.cloud.extension.transform.HotSwapExtMethodTransformer;
import com.netease.cloud.extension.transform.HotSwapExtTransformer;
import org.hotswap.agent.annotation.handler.OnClassLoadedHandler;
import org.hotswap.agent.logging.AgentLogger;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月01日
 * @Version: 1.0
 */
public class OnClassLoadHandler implements ExtTransformHandler<OnClassLoad> {

    protected static AgentLogger LOGGER = AgentLogger.getLogger(OnClassLoadedHandler.class);

    protected HotSwapExtTransformer hotSwapExtTransformer;

    public OnClassLoadHandler(HotSwapExtManager hotSwapExtManager) {
        this.hotSwapExtTransformer = hotSwapExtManager.getHotswapExtTransformer();
        if (hotSwapExtTransformer == null) {
            throw new IllegalArgumentException("Error instantiating OnClassLoadHandler. Hotswap transformer is missing in HotSwapExtManager.");
        }
    }

    @Override
    public boolean initField(final ExtTransformAnnotation<OnClassLoad> extTransformAnnotation) {
        throw new IllegalAccessError("@OnClassLoad annotation not allowed on fields.");
    }

    @Override
    public boolean initMethod(final ExtTransformAnnotation<OnClassLoad> extTransformAnnotation) {
        LOGGER.debug("Init for method " + extTransformAnnotation.getMethod());

        final OnClassLoad annot = extTransformAnnotation.getAnnotation();

        if (annot == null) {
            LOGGER.error("Error in init for method " + extTransformAnnotation.getMethod() + ". Annotation missing.");
            return false;
        }

        hotSwapExtTransformer.registerTransformer(annot.className(), new HotSwapExtMethodTransformer(extTransformAnnotation));
        return true;
    }

}
