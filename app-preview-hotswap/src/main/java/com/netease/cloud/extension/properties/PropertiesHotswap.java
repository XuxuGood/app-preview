package com.netease.cloud.extension.properties;

import com.netease.cloud.extension.IHotExtHandler;
import org.hotswap.agent.logging.AgentLogger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * @Author xiaoxuxuy
 * @Date 2024年02月28日
 * @Version: 1.0
 */
public class PropertiesHotswap implements IHotExtHandler {

    private static AgentLogger logger = AgentLogger.getLogger(PropertiesHotswap.class);

    @Override
    public void afterHandle(ClassLoader classLoader, Class<?> classz, String path, byte[] content) {
        try {
            if (path.endsWith(".properties")) {
                logger.info("当前文件类型为：{}", path);
            } else {
                return;
            }

            File targetPropertyFile = new File(Objects.requireNonNull(classLoader.getResource("application.properties")).getFile());
            logger.info("当前application文件路径：{}", targetPropertyFile.toPath());
            logger.info("待更新application文件内容：{}", new String(content));
            File file = new File(path);
            Files.copy(file.toPath(), targetPropertyFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("更新文件报错", e);
        }
    }

}
