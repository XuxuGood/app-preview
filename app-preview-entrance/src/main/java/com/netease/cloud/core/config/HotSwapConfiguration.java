package com.netease.cloud.core.config;

import com.netease.cloud.HotSwapEntrance;
import org.hotswap.agent.util.HotswapProperties;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * @Author xiaoxuxuy
 * @Date 2024年02月29日
 * @Version: 1.0
 */
public class HotSwapConfiguration {

    private static volatile HotSwapConfiguration instance;

    Properties properties = new HotswapProperties();

    public HotSwapConfiguration() {
    }

    public static HotSwapConfiguration getInstance() {
        if (instance == null) {
            synchronized (HotSwapConfiguration.class) {
                if (instance == null) {
                    instance = new HotSwapConfiguration();
                }
            }
        }
        return instance;
    }

    public void loadConfigurationFile() throws Exception {
        if (HotSwapEntrance.getHotSwapConfigFile() == null || HotSwapEntrance.getHotSwapConfigFile().equals("")) {
            return;
        }

        URL configurationURL = resourceNameToURL(HotSwapEntrance.getHotSwapConfigFile());
        this.properties.load(configurationURL.openStream());

        instance = this;
    }

    private static URL resourceNameToURL(String resource) throws Exception {
        try {
            // Try to format as a URL?
            return new URL(resource);
        } catch (MalformedURLException e) {
            // try to locate a file
            if (resource.startsWith("./"))
                resource = resource.substring(2);

            File file = new File(resource).getCanonicalFile();
            return file.toURI().toURL();
        }
    }

    public Properties getProperties() {
        return properties;
    }

}
