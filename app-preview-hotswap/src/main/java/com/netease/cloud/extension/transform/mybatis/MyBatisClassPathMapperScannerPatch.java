package com.netease.cloud.extension.transform.mybatis;

import com.netease.cloud.extension.annotations.ClassTransform;
import com.netease.cloud.extension.annotations.OnClassLoad;
import org.hotswap.agent.javassist.ClassPool;
import org.hotswap.agent.javassist.CtClass;
import org.hotswap.agent.javassist.CtConstructor;
import org.hotswap.agent.logging.AgentLogger;


/**
 * 插桩获取ClassPathMapperScanner实例
 *
 * @author Liubsyy
 * @date 2023/7/9 8:22 AM
 **/

//@ClassTransform
public class MyBatisClassPathMapperScannerPatch {
    private static AgentLogger logger = AgentLogger.getLogger(MyBatisClassPathMapperScannerPatch.class);

    /**
     *  ClassPathMapperScanner 构造函数插桩，获取ClassPathMapperScanner实例
     */
    @OnClassLoad(className = "org.mybatis.spring.mapper.ClassPathMapperScanner")
    public static void patchMyBatisClassPathMapperScanner(CtClass ctClass, ClassPool classPool){
        logger.info("MyBatisBeanRefresh.patchMyBatisClassPathMapperScanner");
        try{
            CtConstructor constructor = ctClass.getDeclaredConstructor(new CtClass[] {
                    classPool.get("org.springframework.beans.factory.support.BeanDefinitionRegistry") });
            constructor.insertAfter("{com.netease.cloud.extension.transform.mybatis.MyBatisSpringBeanDefinition.loadScanner(this);}");
        }catch (Throwable e) {
            logger.error("patchMyBatisClassPathMapperScanner err",e);
        }
    }

}
