package com.netease.cloud.core.handler;

import com.netease.cloud.core.model.BatchModifiedResourceRequest;
import com.netease.cloud.core.model.HotSwapResponse;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.hotswap.agent.extension.AutoChoose;
import org.hotswap.agent.extension.manager.AllExtensionsManager;
import org.hotswap.agent.logging.AgentLogger;
import org.hotswap.agent.util.JsonUtils;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月04日
 * @Version: 1.0
 */
public class HotSwapResourceFileHandler implements Handler<RoutingContext> {

    private static final AgentLogger LOGGER = AgentLogger.getLogger(HotSwapClassFileHandler.class);

    private final AutoChoose autoChoose;

    public HotSwapResourceFileHandler() {
        autoChoose = new AutoChoose();
    }

    @Override
    public void handle(RoutingContext routingContext) {
        routingContext.request().bodyHandler(requestBody -> {
            String bodyString = requestBody.toString();

            BatchModifiedResourceRequest requestResource = JsonUtils.parse(bodyString, BatchModifiedResourceRequest.class);

            LOGGER.debug("hotswap request params: {}, to pojo: {}", bodyString, requestResource);

            // 获取classloader
            ClassLoader classLoader = AllExtensionsManager.getInstance().getClassLoader();
            if (classLoader == null) {
                classLoader = Thread.currentThread().getContextClassLoader();
            }

            String resourcePath = requestResource.getPath();
            byte[] resourceBytes = requestResource.getContent().getBytes();

            // 前置处理
            autoChoose.preHandle(classLoader, resourcePath, resourceBytes);

            // 将content内容写进path文件中
            try (FileOutputStream fos = new FileOutputStream(resourcePath)) {
                // 将content内容写入到文件中
                fos.write(resourceBytes);
                fos.flush();
            } catch (IOException e) {
                LOGGER.error("Exception writing to file：" + e.getMessage(), e);
                HotSwapResponse errorResponse = HotSwapResponse.of("Exception writing to file", 400, e.getMessage());
                HttpServerResponse response = routingContext.response();
                response.end(JsonObject.mapFrom(errorResponse).toBuffer());
            }

            // 后置处理
            autoChoose.afterHandle(classLoader, null, resourcePath, resourceBytes);

            HotSwapResponse successResponse = HotSwapResponse.success("resource updated successfully");
            HttpServerResponse response = routingContext.response();
            response.end(JsonObject.mapFrom(successResponse).toBuffer());
        });
    }

}