package com.netease.cloud.extension.transform.spring;

import com.netease.cloud.extension.annotations.ClassTransform;
import com.netease.cloud.extension.annotations.OnClassLoad;
import com.netease.cloud.extension.cache.SpringMVCJacksonCacheClear;
import com.netease.cloud.extension.manager.AllExtensionsManager;
import org.hotswap.agent.logging.AgentLogger;

/**
 * @author Liubsyy
 * @date 2024/1/1
 **/
@ClassTransform
public class SpringMVCJacksonRegistry {
    private static AgentLogger logger = AgentLogger.getLogger(SpringMVCJacksonRegistry.class);

    @OnClassLoad(className = "com.fasterxml.jackson.databind.ser.impl.ReadOnlyClassToSerializerMap")
    public static void registryReadOnlyClassToSerializerMap() {
        AllExtensionsManager.getInstance().addHotExtHandler(new SpringMVCJacksonCacheClear());
    }

    @OnClassLoad(className = "com.fasterxml.jackson.databind.ObjectMapper")
    public static void registryObjectMapper() {
        AllExtensionsManager.getInstance().addHotExtHandler(new SpringMVCJacksonCacheClear());
    }
}
