package com.netease.cloud.extension.transform;

import org.hotswap.agent.config.PluginManager;

import java.lang.instrument.Instrumentation;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月01日
 * @Version: 1.0
 */
public class HotSwapExtManager {

    private static HotSwapExtManager INSTANCE = new HotSwapExtManager();

    public static HotSwapExtManager getInstance() {
        return INSTANCE;
    }

    protected HotSwapExtTransformer hotswapTransformer;

    private HotSwapExtTransformerRegistry extTransformerRegistry;

    private HotSwapExtManager() {
        hotswapTransformer = new HotSwapExtTransformer();
        extTransformerRegistry = new HotSwapExtTransformerRegistry(this);
    }

    public void init(Instrumentation instrumentation) {
        // 增加热部署扩展动态字节码转换
        instrumentation.addTransformer(new HotSwapExtTransformer());
        System.out.println(111);
    }

}
