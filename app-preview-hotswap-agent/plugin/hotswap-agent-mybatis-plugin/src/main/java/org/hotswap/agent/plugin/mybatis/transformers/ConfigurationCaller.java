package org.hotswap.agent.plugin.mybatis.transformers;

import org.hotswap.agent.util.ReflectionHelper;
import org.hotswap.agent.plugin.mybatis.MyBatisPlugin;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月08日
 * @Version: 1.0
 */
public class ConfigurationCaller {

    public static void removeMappedStatements(Object factoryBean) {
         ReflectionHelper.invoke(factoryBean, MyBatisPlugin.CONFIGURATION_MAPPED_STATEMENT_PROXY_METHOD);
    }

}
