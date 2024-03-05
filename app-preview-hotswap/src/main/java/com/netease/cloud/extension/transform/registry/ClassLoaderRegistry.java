package com.netease.cloud.extension.transform.registry;

import com.netease.cloud.extension.annotations.ClassTransform;
import com.netease.cloud.extension.annotations.OnClassLoad;
import com.netease.cloud.extension.transform.HotSwapExtManager;
import org.hotswap.agent.logging.AgentLogger;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月05日
 * @Version: 1.0
 */
@ClassTransform
public class ClassLoaderRegistry {

    private static final AgentLogger LOGGER = AgentLogger.getLogger(VelocityRegistry.class);

    @OnClassLoad(className = "org.springframework.boot.SpringApplication")
    public static void registrySpringBootClassLoader() {
        // 注册SpringBoot的ClassLoader
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        LOGGER.info("registrySpringBootClassLoader contextClassLoader:{}", contextClassLoader);
        HotSwapExtManager.getInstance().setClassLoader(contextClassLoader);
    }

}
