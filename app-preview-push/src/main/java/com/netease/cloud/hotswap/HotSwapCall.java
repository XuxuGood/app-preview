package com.netease.cloud.hotswap;

import com.netease.cloud.model.BatchModifiedClassRequest;
import com.netease.cloud.model.BatchModifiedResourceRequest;
import com.netease.cloud.utils.JsonUtils;
import okhttp3.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author xiaoxuxuy
 * @Date 2024年02月29日
 * @Version: 1.0
 */
public class HotSwapCall {

    public static void main(String[] args) throws IOException {
        // 热更新配置文件
        hotswapResource();
        // 热更新类
        hotswapClass();
    }

    private static void hotswapClass() throws IOException {
        // 远程调用
        OkHttpClient client = MyHttpClient.getClient();

        // 热更新类
        List<BatchModifiedClassRequest> requestList = new ArrayList<>();

        BatchModifiedClassRequest modifiedRequest = new BatchModifiedClassRequest();
        //热更新类名
        modifiedRequest.setClassName("com.netease.cloud.controller.TestController");
        //热更新的字节码
        modifiedRequest.setBytes(Files.readAllBytes(Paths.get("/Users/xiaoxuxuy/Desktop/工作/网易/项目/低代码/app-preview/app-preview-push/target/classes/com/netease/cloud/controller/TestController.class")));
        requestList.add(modifiedRequest);

        modifiedRequest = new BatchModifiedClassRequest();
        //热更新类名
        modifiedRequest.setClassName("com.netease.cloud.controller.Test2");
        //热更新的字节码
        modifiedRequest.setBytes(Files.readAllBytes(Paths.get("/Users/xiaoxuxuy/Desktop/工作/网易/项目/低代码/app-preview/app-preview-push/target/classes/com/netease/cloud/controller/Test2.class")));
        requestList.add(modifiedRequest);

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(JsonUtils.toJsonString(requestList), mediaType);

        Request request = new Request.Builder()
//                .url("http://localhost:8090/app-preview/hotswap/class")
                .url("http://152.136.181.95:8012/app-preview/hotswap/class")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        System.out.println("hotswap response:" + Objects.requireNonNull(response.body()).string());
    }

    private static void hotswapResource() throws IOException {
        // 远程调用
        OkHttpClient client = MyHttpClient.getClient();

        BatchModifiedResourceRequest resourceRequest = new BatchModifiedResourceRequest();
//        resourceRequest.setPath("/Users/xiaoxuxuy/Desktop/工作/网易/项目/低代码/app-preview/app-preview-push/target/classes/application.properties");
        resourceRequest.setPath("/www/app-preview/app/BOOT-INF/classes/application.properties");
        resourceRequest.setContent(new String(Files.readAllBytes(Paths.get("/Users/xiaoxuxuy/Desktop/工作/网易/项目/低代码/app-preview/app-preview-push/src/main/resources/application.properties"))));

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(JsonUtils.toJsonString(resourceRequest), mediaType);

        Request request = new Request.Builder()
//                .url("http://localhost:8090/app-preview/hotswap/resource")
                .url("http://152.136.181.95:8012/app-preview/hotswap/resource")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        System.out.println("hotswap response:" + Objects.requireNonNull(response.body()).string());
    }

}
