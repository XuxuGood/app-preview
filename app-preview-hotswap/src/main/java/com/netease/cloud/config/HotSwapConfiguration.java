package com.netease.cloud.config;

import com.netease.cloud.HotSwapEntrance;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

    public String remotePort;
    public String classloader;
    public String devExtClassName;

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
        // 通过DocumentBuilder对象的parse方法加载配置文件
        Document document = builder.parse(new File(HotSwapEntrance.getHotSwapConfigFile()));

        // 获取根节点
        Element root = document.getDocumentElement();

        // 获取remote_port节点的值
        this.remotePort = root.getElementsByTagName("remote_port").item(0).getTextContent();

        // 获取classloader节点的值
        this.classloader = root.getElementsByTagName("classloader").item(0).getTextContent();

        // 获取dev-ext节点下的classname节点的值
        NodeList devExtList = root.getElementsByTagName("dev-ext");
        if (devExtList.getLength() > 0) {
            Element devExt = (Element) devExtList.item(0);
            this.devExtClassName = devExt.getElementsByTagName("classname").item(0).getTextContent();
        }

        instance = this;
    }

    public String getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(String remotePort) {
        this.remotePort = remotePort;
    }

    public String getClassloader() {
        return classloader;
    }

    public void setClassloader(String classloader) {
        this.classloader = classloader;
    }

    public String getDevExtClassName() {
        return devExtClassName;
    }

    public void setDevExtClassName(String devExtClassName) {
        this.devExtClassName = devExtClassName;
    }

}
