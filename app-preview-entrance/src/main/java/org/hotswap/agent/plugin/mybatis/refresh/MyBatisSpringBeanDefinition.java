package org.hotswap.agent.plugin.mybatis.refresh;

import org.hotswap.agent.extension.manager.AllExtensionsManager;
import org.hotswap.agent.logging.AgentLogger;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * mybatis的生成代理对象的定义注入到spring中
 **/
public class MyBatisSpringBeanDefinition {
    private static final AgentLogger logger = AgentLogger.getLogger(MyBatisSpringBeanDefinition.class);
    private static ClassPathMapperScanner mapperScanner;
    private static String scanBasePackages;

    public static ClassPathMapperScanner getMapperScanner() {
        return mapperScanner;
    }

    public static void loadScanner(ClassPathMapperScanner scanner) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException {
        if (null != mapperScanner) {
            return;
        }
        mapperScanner = scanner;
        AllExtensionsManager.getInstance().addHotExtHandler(new MyBatisBeanRefresh(scanBasePackages));
    }

    public static void loadBasePackages(String basePackages) {
        if (null != scanBasePackages) {
            return;
        }
        scanBasePackages = basePackages;
    }

    /**
     * 这块是mybatis接口的生成代理类的原理
     *
     * @param holder
     */
    public static void mybatisBeanDefinition(BeanDefinitionHolder holder) {
        if (null == mapperScanner) {
            return;
        }
        try {
            Set<BeanDefinitionHolder> holders = new HashSet<>();
            holders.add(holder);
            Method method = Class.forName("org.mybatis.spring.mapper.ClassPathMapperScanner")
                    .getDeclaredMethod("processBeanDefinitions", Set.class);
            boolean isAccess = method.isAccessible();
            method.setAccessible(true);
            method.invoke(mapperScanner, holders);
            method.setAccessible(isAccess);
        } catch (Exception e) {
            logger.error("freshMyBatis err", e);
        }
    }
}
