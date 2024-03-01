package com.netease.cloud;

import com.alibaba.fastjson.JSON;
import com.netease.cloud.model.BatchModifiedRequest;
import com.netease.cloud.model.HotSwapResponse;
import com.netease.cloud.service.IHotDeployService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author xiaoxuxuy
 * @Date 2024年02月29日
 * @Version: 1.0
 */
public class HotSwapCall {

    public static void main(String[] args) throws NotBoundException, IOException {

        String remoteIp = "127.0.0.1";
        int remotePort = 2024;
        int timeout = 3000; //ms

        Registry registry = LocateRegistry.getRegistry(remoteIp,
                remotePort, (host, port) -> {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(host, port), timeout);
                    return socket;
                });
        IHotDeployService hotDeploy = (IHotDeployService) registry.lookup("HotDeployService");

        // 更新配置文件
//        HotSwapResponse response = hotDeploy.uploadResourceFile("/www/hotswap/start/BOOT-INF/classes/application.properties",
//                new String(Files.readAllBytes(Paths.get("/Users/xiaoxuxuy/Desktop/工作/网易/项目/制品库测试项目/artifactory-maven/src/main/resources/application.properties")), StandardCharsets.ISO_8859_1));
//        System.out.println("HotSeconds response:" + JSON.toJSONString(response));

        HotSwapResponse response = hotDeploy.uploadResourceFile("/Users/xiaoxuxuy/Desktop/工作/网易/项目/低代码/app-preview/app-preview-push/target/application.properties",
                new String(Files.readAllBytes(Paths.get("/Users/xiaoxuxuy/Desktop/工作/网易/项目/制品库测试项目/artifactory-maven/src/main/resources/application.properties")), StandardCharsets.ISO_8859_1));
        System.out.println("HotSeconds response:" + JSON.toJSONString(response));

        List<BatchModifiedRequest> requestList = new ArrayList<>();

        BatchModifiedRequest request = new BatchModifiedRequest();
        //热更新类名
        request.setClassName("com.netease.cloud.controller.TestController");
        //热更新的字节码
        request.setBytes(Files.readAllBytes(Paths.get("/Users/xiaoxuxuy/Desktop/工作/网易/项目/低代码/app-preview/app-preview-push/target/classes/com/netease/cloud/controller/TestController.class")));
        requestList.add(request);

        HotSwapResponse hotSwapResponse = hotDeploy.batchHotswapModifiedJava(requestList);

        System.out.println("hotswap response:" + JSON.toJSONString(hotSwapResponse));
    }

}
