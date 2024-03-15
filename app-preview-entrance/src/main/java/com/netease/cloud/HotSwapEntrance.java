package com.netease.cloud;

import com.netease.cloud.core.config.HotSwapConfiguration;
import io.vertx.core.Vertx;
import org.hotswap.agent.HotswapAgent;
import org.hotswap.agent.logging.AgentLogger;
import org.hotswap.agent.util.spring.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * @Author xiaoxuxuy
 * @Date 2024年02月29日
 * @Version: 1.0
 */
public class HotSwapEntrance {

    private static final AgentLogger LOGGER = AgentLogger.getLogger(HotswapAgent.class);

    private static String propertiesFilePath;

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
        // 初始化扩展类路径目录
        initHotswapDir(HotSwapConfiguration.getInstance().getProperties().getProperty("extraClasspath"));
        // 初始化热部署资源目录
        initWatchResourcesDir(HotSwapConfiguration.getInstance().getProperties().getProperty("watchResources"));
        // 启动热部署agent
        HotswapAgent.agentmain(args, inst);
        // 启动 vertx http 服务
        Vertx.vertx().deployVerticle(new VertxApplication());
    }

    private static void initWatchResourcesDir(String watchResources) {
        if (StringUtils.isEmpty(watchResources)) {
            return;
        }
        String[] watchResourceDirs = watchResources.split(",");

        for (String watchResourceDir : watchResourceDirs) {
            initHotswapDir(watchResourceDir);
        }
    }

    /**
     * 初始热更新目录
     */
    private static void initHotswapDir(String dirPath) {
        if (StringUtils.isEmpty(dirPath)) {
            return;
        }

        // 创建目录
        File directory = new File(dirPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                LOGGER.info(dirPath + " directory created successfully.");
            }
        } else {
            LOGGER.info(dirPath + " directory already exists.");
            // 删除旧目录内容
            cleanupDirectory(dirPath);
        }
    }

    private static void cleanupDirectory(String cleanDirectory) {
        Path directory = Paths.get(cleanDirectory);

        try (Stream<Path> walk = Files.walk(directory)) {
            walk.filter(path -> !path.equals(directory))
                    .sorted((p1, p2) -> -p1.compareTo(p2))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
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

            if ("propertiesFilePath".equals(option)) {
                propertiesFilePath = optionValue;
            }
        }
    }

    /**
     * 获取热部署配置文件路径
     *
     * @return
     */
    public static String getHotSwapConfigFile() {
        return propertiesFilePath;
    }

}
