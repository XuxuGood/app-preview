package com.netease.cloud.extension.transform.registry;

import com.netease.cloud.extension.annotations.ClassTransform;
import com.netease.cloud.extension.annotations.OnClassLoad;
import com.netease.cloud.extension.cache.VelocityHtmlCacheClear;
import com.netease.cloud.extension.logging.Logger;
import com.netease.cloud.extension.manager.AllExtensionsManager;

/**
 * @author Liubsyy
 * @date 2023/7/8 9:17 PM
 * 当检测到org.apache.velocity.runtime.RuntimeSingleton类加载时，那就是项目里使用了velocity
 **/
@ClassTransform
public class VelocityRegistry {
    private static Logger logger = Logger.getLogger(VelocityRegistry.class);

    @OnClassLoad(className = "org.apache.velocity.runtime.RuntimeSingleton")
    public static void registryOnClass() {
        AllExtensionsManager.getInstance().addHotExtHandler(new VelocityHtmlCacheClear());
    }
}
