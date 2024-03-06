package org.hotswap.agent.plugin.mybatis.transformers;

import org.hotswap.agent.logging.AgentLogger;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.config.BeanDefinitionHolder;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * mybatis的生成代理对象的定义注入到spring中
 * @author Liubsyy
 * @date 2023/7/9 10:17 PM
 **/
public class MyBatisSpringBeanDefinition {
    private static AgentLogger logger = AgentLogger.getLogger(MyBatisSpringBeanDefinition.class);
    private static ClassPathMapperScanner mapperScanner;


    public static ClassPathMapperScanner getMapperScanner() {
        return mapperScanner;
    }


    public static void loadScanner(ClassPathMapperScanner scanner) {
        if(null != mapperScanner) {
            return;
        }
        mapperScanner = scanner;
        MyBatisBeanRefresh myBatisBeanRefresh = new MyBatisBeanRefresh();
        System.out.println(1111);
    }


    /**
     * 这块是mybatis接口的生成代理类的原理
     * @param holder
     */
    public static void mybatisBeanDefinition(BeanDefinitionHolder holder){
        if(null == mapperScanner) {
            return;
        }
        try{
            Set<BeanDefinitionHolder> holders = new HashSet<>();
            holders.add(holder);
            Method method = Class.forName("org.mybatis.spring.mapper.ClassPathMapperScanner")
                    .getDeclaredMethod("processBeanDefinitions", Set.class);
            boolean isAccess = method.isAccessible();
            method.setAccessible(true);
            method.invoke(mapperScanner, holders);
            method.setAccessible(isAccess);
        }catch (Exception e) {
            logger.error("freshMyBatis err",e);
        }
    }
}
