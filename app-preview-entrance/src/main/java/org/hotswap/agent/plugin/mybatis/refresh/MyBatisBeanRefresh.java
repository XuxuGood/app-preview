package org.hotswap.agent.plugin.mybatis.refresh;

import org.apache.ibatis.session.Configuration;
import org.hotswap.agent.extension.IHotExtHandler;
import org.hotswap.agent.logging.AgentLogger;
import org.hotswap.agent.plugin.spring.scanner.ClassPathBeanDefinitionScannerAgent;
import org.hotswap.agent.util.ReflectUtil;
import org.hotswap.agent.util.ReflectionHelper;
import org.hotswap.agent.util.spring.util.CollectionUtils;
import org.hotswap.agent.util.spring.util.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 新增mapper接口热部署
 * 当新增一个mapper接口的时候，给mapper接口生成代理对象并注册到spring中
 */
public class MyBatisBeanRefresh implements IHotExtHandler {
    private static final AgentLogger LOGGER = AgentLogger.getLogger(MyBatisBeanRefresh.class);

    private List<String> scanBasePackages;

    public MyBatisBeanRefresh(String scanBasePackages) {
        if (StringUtils.isEmpty(scanBasePackages)) {
            return;
        }
        this.scanBasePackages = Arrays.asList(scanBasePackages.split(","));
    }

    public void afterHandle(ClassLoader classLoader, Class<?> classz, String path, byte[] bytes) {
        if (classz == null || !classz.isInterface() || MyBatisSpringBeanDefinition.getMapperScanner() == null) {
            return;
        }

        boolean needSwap = classz.getAnnotation(org.apache.ibatis.annotations.Mapper.class) != null;
        if (!CollectionUtils.isEmpty(scanBasePackages) && !needSwap) {
            if (scanBasePackages.stream().anyMatch(scanBasePackage -> classz.getName().startsWith(scanBasePackage))) {
                needSwap = true;
            }
        }

        if (needSwap) {
            swapMapper(classLoader, classz, bytes);
        }
    }

    private void swapMapper(ClassLoader classLoader, Class<?> classz, byte[] bytes) {
        try {
            Class<?> sqlSessionFactoryClz = Class.forName("org.apache.ibatis.session.defaults.DefaultSqlSessionFactory", true, classLoader);
            Field staticConfiguration;

            try {
                staticConfiguration = sqlSessionFactoryClz.getDeclaredField("_staticConfiguration");
            } catch (NoSuchFieldException ex) {
                return;
            }

            Configuration configuration = ((ArrayList<Configuration>) staticConfiguration.get(null)).get(0);

            //这里用类字符串判断是否mybatis plus，不引用mybatis plus的类，避免应用程序没有用mybatis plus而报错
            if (configuration.getClass().getName().equals("com.baomidou.mybatisplus.core.MybatisConfiguration")) {
                MyBatisPlusMapperUpdate.refreshMapper(configuration, classz);
                // return;
            }


            ClassPathBeanDefinitionScannerAgent scannerAgent = ClassPathBeanDefinitionScannerAgent.getInstance(MyBatisSpringBeanDefinition.getMapperScanner());
            BeanDefinition beanDefinition = scannerAgent.resolveBeanDefinition(classLoader, bytes);
            if (beanDefinition != null) {
                try {
                    scannerAgent.defineBean(beanDefinition);
                } catch (Exception e) {
                    LOGGER.warning("mybatis define bean err, ignore this situation for now");
                }
            }

            // bean name
            BeanNameGenerator beanNameGenerator = (BeanNameGenerator) ReflectionHelper.get(MyBatisSpringBeanDefinition.getMapperScanner(), "beanNameGenerator");
            BeanDefinitionRegistry registry = ReflectUtil.getField(scannerAgent, "registry");
            String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);

            // beanDefinitionHolder
            BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);

            MyBatisSpringBeanDefinition.mybatisBeanDefinition(definitionHolder);
        } catch (Exception e) {
            LOGGER.error("refresh mybatis bean err", e);
        }
    }

}
