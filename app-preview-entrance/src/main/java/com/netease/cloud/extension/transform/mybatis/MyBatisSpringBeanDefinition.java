package com.netease.cloud.extension.transform.mybatis;

import com.netease.cloud.extension.container.MyBatisBeanRefresh;
import com.netease.cloud.extension.manager.AllExtensionsManager;
import org.hotswap.agent.logging.AgentLogger;
import org.mybatis.spring.mapper.ClassPathMapperScanner;

/**
 * mybatis的生成代理对象的定义注入到spring中
 * @author Liubsyy
 * @date 2023/7/9 10:17 PM
 **/
public class MyBatisSpringBeanDefinition {
    private static AgentLogger logger = AgentLogger.getLogger(MyBatisClassPathMapperScannerPatch.class);
    private static ClassPathMapperScanner mapperScanner;

    public static void loadScanner(ClassPathMapperScanner scanner) {
        if(null != mapperScanner) {
            return;
        }
        mapperScanner = scanner;
        AllExtensionsManager.getInstance().addHotExtHandler(new MyBatisBeanRefresh(mapperScanner));
    }

}
