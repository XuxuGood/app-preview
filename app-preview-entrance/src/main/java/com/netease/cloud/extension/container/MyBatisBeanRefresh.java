package com.netease.cloud.extension.container;

import com.netease.cloud.extension.IHotExtHandler;
import com.netease.cloud.extension.transform.mybatis.MyBatisClassPathMapperScannerPatch;
import com.netease.cloud.extension.transform.mybatis.MyBatisSpringBeanDefinition;
import com.netease.cloud.extension.util.ReflectUtil;
import org.apache.ibatis.session.Configuration;
import org.hotswap.agent.logging.AgentLogger;
import org.hotswap.agent.plugin.spring.scanner.ClassPathBeanDefinitionScannerAgent;
import org.hotswap.agent.util.ReflectionHelper;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * 新增mapper接口热部署
 * 当新增一个mapper接口的时候，给mapper接口生成代理对象并注册到spring中
 *
 * @author Liubsyy
 * @date 2023/7/9 9:22 PM
 */
public class MyBatisBeanRefresh implements IHotExtHandler {
    private static AgentLogger logger = AgentLogger.getLogger(MyBatisClassPathMapperScannerPatch.class);

    private final ClassPathMapperScanner mapperScanner;

    public MyBatisBeanRefresh(ClassPathMapperScanner mapperScanner) {
        logger.info("MyBatisBeanRefresh init");
        this.mapperScanner = mapperScanner;
    }

    @Override
    public void afterHandle(ClassLoader classLoader, Class<?> classz, String path, byte[] bytes) {
        if (classz == null || !classz.isInterface()) {
            return;
        }
        if (classz.getAnnotation(org.apache.ibatis.annotations.Mapper.class) == null) {
            return;
        }
        if (null == this.mapperScanner) {
            return;
        }
        try {
            Class<?> sqlSessionFactoryClz = Class.forName("org.apache.ibatis.session.defaults.DefaultSqlSessionFactory", true, classLoader);
            Field staticConfiguration = null;
            try {
                staticConfiguration = sqlSessionFactoryClz.getDeclaredField("_staticConfiguration");
            } catch (NoSuchFieldException ex) {
                return;
            }
            Configuration configuration = ((ArrayList<Configuration>) staticConfiguration.get(null)).get(0);


            //这里用类字符串判断是否mybatis plus，不引用mybatis plus的类，避免应用程序没有用mybatis plus而报错
            if (configuration.getClass().getName().equals("com.baomidou.mybatisplus.core.MybatisConfiguration")) {
                MyBatisPlusMapperUpdate.refreshMapper(configuration, classz);
                //return;
            }


            ClassPathBeanDefinitionScannerAgent scannerAgent = ClassPathBeanDefinitionScannerAgent.getInstance(mapperScanner);
            BeanDefinition beanDefinition = scannerAgent.resolveBeanDefinition(classLoader, bytes);
            if (beanDefinition != null) {
                scannerAgent.defineBean(beanDefinition);
            }

            //bean name
            BeanNameGenerator beanNameGenerator = (BeanNameGenerator) ReflectionHelper.get(mapperScanner, "beanNameGenerator");
            BeanDefinitionRegistry registry = ReflectUtil.getField(scannerAgent, "registry");
            String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);

            //beanDefinitionHolder
            BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);

            mybatisBeanDefinition(definitionHolder);
        } catch (Exception e) {
            logger.error("Refresh Mybatis Bean err", e);
        }
    }


    /**
     * 这块是mybatis接口的生成代理类的原理
     *
     * @param holder
     */
    public void mybatisBeanDefinition(BeanDefinitionHolder holder) {
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
