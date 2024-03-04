package com.netease.cloud.core.config;

import com.netease.cloud.HotSwapEntrance;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * @Author xiaoxuxuy
 * @Date 2024年02月29日
 * @Version: 1.0
 */
public class HotSwapConfiguration {

    private static volatile HotSwapConfiguration instance;

    public int remotePort;

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
        // 创建一个DocumentBuilderFactory对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // 创建一个DocumentBuilder对象
        DocumentBuilder builder = factory.newDocumentBuilder();

        if (HotSwapEntrance.getHotSwapConfigFile() == null || HotSwapEntrance.getHotSwapConfigFile().equals("")) {
            return;
        }

        // 通过DocumentBuilder对象的parse方法加载配置文件
        Document document = builder.parse(new File(HotSwapEntrance.getHotSwapConfigFile()));

        // 获取根节点
        Element root = document.getDocumentElement();

        // 获取remote_port节点的值
        this.remotePort = Integer.parseInt(root.getElementsByTagName("remote_port").item(0).getTextContent());

        instance = this;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

}
