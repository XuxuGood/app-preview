package com.netease.cloud;

import com.netease.cloud.config.HotSwapConfiguration;
import com.netease.cloud.extension.transform.HotSwapExtManager;
import com.netease.cloud.extension.transform.HotSwapExtTransformer;
import com.netease.cloud.extension.transform.demo.Boy;
import com.netease.cloud.service.IHotDeployService;
import com.netease.cloud.service.impl.IHotDeployServiceImpl;
import org.hotswap.agent.HotswapAgent;
import org.hotswap.agent.logging.AgentLogger;

import java.lang.instrument.Instrumentation;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @Author xiaoxuxuy
 * @Date 2024年02月29日
 * @Version: 1.0
 */
public class HotSwapEntrance {

    private static final AgentLogger LOGGER = AgentLogger.getLogger(HotswapAgent.class);

    private static String hotSwapConfigFilePath;

    public static void premain(String args, Instrumentation inst) throws Exception {
        LOGGER.info("hotswap start...");
        // 解析参数
        parseArgs(args);
        // 加载配置文件
        HotSwapConfiguration.getInstance().loadConfigurationFile();
        // 注册热部署远程调用服务
        registryHotDeployService();
        // 初始化热部署扩展
        HotSwapExtManager.getInstance().init(inst);
        // 启动热部署agent
        HotswapAgent.agentmain(args, inst);

        Boy boy = new Boy();
        boy.printAll();
    }

    /**
     * 注册热部署远程调用服务
     */
    private static void registryHotDeployService() throws RemoteException {
        // 创建远程对象实例
        IHotDeployService hotDeployService = new IHotDeployServiceImpl();
        // 导出远程对象，绑定到指定端口
        Registry registry = LocateRegistry.createRegistry(HotSwapConfiguration.getInstance().getRemotePort());
        registry.rebind("HotDeployService", hotDeployService);
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
