package com.netease.cloud.extension.transform.registry;

import com.netease.cloud.extension.annotations.ClassTransform;
import com.netease.cloud.extension.annotations.OnClassLoad;
import com.netease.cloud.extension.logging.Logger;
import com.netease.cloud.extension.manager.AllExtensionsManager;
import com.netease.cloud.extension.properties.PropertiesHotswap;

/**
 * @Author xiaoxuxuy
 * @Date 2024年02月27日
 * @Version: 1.0
 */
@ClassTransform
public class PropertiesHotswapRegistry {

    private static Logger logger = Logger.getLogger(VelocityRegistry.class);

    @OnClassLoad(className = "org.springframework.boot.SpringApplication")
    public static void registryOnClass() {
        AllExtensionsManager.getInstance().addHotExtHandler(new PropertiesHotswap());
    }

}
