package org.hotswap.agent.plugin.mybatis.proxy;

import org.apache.ibatis.session.Configuration;
import org.hotswap.agent.javassist.util.proxy.MethodHandler;
import org.hotswap.agent.javassist.util.proxy.ProxyFactory;
import org.hotswap.agent.logging.AgentLogger;
import org.hotswap.agent.util.ReflectionHelper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.*;

public class SpringMybatisConfigurationProxy {
    private static final AgentLogger LOGGER = AgentLogger.getLogger(SpringMybatisConfigurationProxy.class);

    private static Map<Object, SpringMybatisConfigurationProxy> proxiedConfigurations = new HashMap<>();

    public SpringMybatisConfigurationProxy(Object sqlSessionFactoryBean) {
        this.sqlSessionFactoryBean = sqlSessionFactoryBean;
    }

    public static SpringMybatisConfigurationProxy getWrapper(Object sqlSessionFactoryBean) {
        if (!proxiedConfigurations.containsKey(sqlSessionFactoryBean)) {
            proxiedConfigurations.put(sqlSessionFactoryBean, new SpringMybatisConfigurationProxy(sqlSessionFactoryBean));
        }
        return proxiedConfigurations.get(sqlSessionFactoryBean);
    }

    public static void refreshProxiedConfigurations(String xmlPath) {
        for (SpringMybatisConfigurationProxy wrapper : proxiedConfigurations.values())
            try {
                wrapper.refreshProxiedConfiguration(xmlPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void refreshProxiedConfiguration(String xmlPath) throws IOException {
        // 判断是否是Mybatis的xml
        if (!isMyBatisXmlFile(xmlPath)) {
            return;
        }

        Resource[] resources = (Resource[]) ReflectionHelper.invoke(this.sqlSessionFactoryBean, "getMapperLocations");
        List<Resource> oldResourceList = new ArrayList<>(Arrays.asList(resources));

        // 迭代移除已经存在的xml文件
        Iterator<Resource> iterator = oldResourceList.iterator();
        while (iterator.hasNext()) {
            Resource resource = iterator.next();
            String path;
            if (resource instanceof ClassPathResource) {
                ClassPathResource classPathResource = (ClassPathResource) resource;
                path = classPathResource.getPath();
            } else if (resource instanceof FileSystemResource) {
                FileSystemResource fileSystemResource = (FileSystemResource) resource;
                path = fileSystemResource.getPath();
            } else {
                path = resource.getURI().getPath();
            }

            if (xmlPath.contains(path)) {
                iterator.remove();
            }
        }

        // 添加新的xml文件
        List<Resource> newResourceList = new ArrayList<>(oldResourceList);
        newResourceList.add(new FileSystemResource(xmlPath));

        Resource[] newResources = newResourceList.toArray(new Resource[0]);

        LOGGER.debug("new mapper locations is {}", (Object) newResources);

        // 重新设置MapperLocations
        ReflectionHelper.invoke(this.sqlSessionFactoryBean, this.sqlSessionFactoryBean.getClass(),
                "setMapperLocations", new Class[]{Resource[].class}, (Object) newResources);

        Object newSqlSessionFactory = ReflectionHelper.invoke(this.sqlSessionFactoryBean, "buildSqlSessionFactory");
        this.configuration = (Configuration) ReflectionHelper.get(newSqlSessionFactory, "configuration");
    }

    private boolean isMyBatisXmlFile(String xmlPath) {
        // 读取文件内容
        try (BufferedReader reader = new BufferedReader(new FileReader(xmlPath))) {
            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
            }
            String fileContentString = fileContent.toString();
            // 判断文件内容是否包含MyBatis的SQL映射配置标签。这里先简易判断，后续可以优化
            if (fileContentString.contains("<mapper") && fileContentString.contains("mybatis") && fileContentString.contains("namespace")) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Object sqlSessionFactoryBean;
    private Configuration configuration;
    private Configuration proxyInstance;

    public Configuration proxy(Configuration origConfiguration) {
        this.configuration = origConfiguration;
        if (proxyInstance == null) {
            ProxyFactory factory = new ProxyFactory();
            factory.setSuperclass(Configuration.class);

            MethodHandler handler = new MethodHandler() {
                @Override
                public Object invoke(Object self, Method overridden, Method forwarder,
                                     Object[] args) throws Throwable {
                    return overridden.invoke(configuration, args);
                }
            };

            try {
                proxyInstance = (Configuration) factory.create(new Class[0], null, handler);
            } catch (Exception e) {
                throw new Error("Unable instantiate Configuration proxy", e);
            }
        }
        return proxyInstance;
    }


}
