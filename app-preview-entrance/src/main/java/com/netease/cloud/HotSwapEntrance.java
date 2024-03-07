package com.netease.cloud;

import com.netease.cloud.core.config.HotSwapConfiguration;
import io.vertx.core.Vertx;
import org.hotswap.agent.HotswapAgent;
import org.hotswap.agent.logging.AgentLogger;

import java.lang.instrument.Instrumentation;

/**
 * @Author xiaoxuxuy
 * @Date 2024年02月29日
 * @Version: 1.0
 */
public class HotSwapEntrance {

    private static final AgentLogger LOGGER = AgentLogger.getLogger(HotswapAgent.class);

    private static String hotSwapConfigFilePath;

    /**
     * 热部署入口
     *
     * @param args 参数
     * @param inst 字节码增强
     * @throws Exception 异常
     */
    public static void premain(String args, Instrumentation inst) throws Exception {
        // 解析参数
        parseArgs(args);
        // 加载配置文件
        HotSwapConfiguration.getInstance().loadConfigurationFile();
        // 启动热部署agent
        HotswapAgent.agentmain(args, inst);
        // 启动 vertx http 服务
        Vertx.vertx().deployVerticle(new VertxApplication(inst));
    }

    /**
     * 解析参数
     *
     * @param args
     */
    private static void parseArgs(String args) {
        if (args == null) {
            return;
        }

        for (String arg : args.split(",")) {
            String[] val = arg.split("=");
            if (val.length != 2) {
                LOGGER.warning("Invalid javaagent command line argument '{}'. Argument is ignored.", arg);
            }

            String option = val[0];
            String optionValue = val[1];

            if ("hotConf".equals(option)) {
                hotSwapConfigFilePath = optionValue;
            }
        }
    }

    /**
     * 获取热部署配置文件路径
     *
     * @return
     */
    public static String getHotSwapConfigFile() {
        return hotSwapConfigFilePath;
    }

}
