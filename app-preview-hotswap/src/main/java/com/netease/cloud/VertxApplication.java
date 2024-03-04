package com.netease.cloud;

import com.netease.cloud.core.config.HotSwapConfiguration;
import com.netease.cloud.core.handler.HotSwapClassFileHandler;
import com.netease.cloud.core.handler.HotSwapResourceFileHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import org.hotswap.agent.HotswapAgent;
import org.hotswap.agent.logging.AgentLogger;

/**
 * Vertx Web 启动类
 *
 * @Author xiaoxuxuy
 * @Date 2024年03月04日
 * @Version: 1.0
 */
public class VertxApplication extends AbstractVerticle {

    private static final AgentLogger LOGGER = AgentLogger.getLogger(HotswapAgent.class);

    private static final int defaultPort = 8090;

    /**
     * 启动vertx web
     *
     * @throws Exception 异常
     */
    @Override
    public void start() throws Exception {
        int remotePort = HotSwapConfiguration.getInstance().getRemotePort();
        if (remotePort <= 0) {
            remotePort = defaultPort;
        }

        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.post("/app-preview/hotswap/class").handler(new HotSwapClassFileHandler());
        router.post("/app-preview/hotswap/resource").handler(new HotSwapResourceFileHandler());
        httpServer.requestHandler(router)
                .listen(remotePort)
                .onSuccess(server -> LOGGER.info("hotswap http server started on port " + server.actualPort()))
                .onFailure(cause -> LOGGER.error("hotswap http server failed to start", cause));
    }

}
