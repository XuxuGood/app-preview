package com.netease.cloud.service.impl;

import com.netease.cloud.model.BatchModifiedRequest;
import com.netease.cloud.model.HotSwapResponse;
import com.netease.cloud.service.IHotDeployService;
import org.hotswap.agent.HotswapAgent;
import org.hotswap.agent.annotation.OnClassLoadEvent;
import org.hotswap.agent.logging.AgentLogger;
import org.hotswap.agent.util.HotswapTransformer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * @Author xiaoxuxuy
 * @Date 2024年02月29日
 * @Version: 1.0
 */
public class IHotDeployServiceImpl extends UnicastRemoteObject implements IHotDeployService {

    private static final AgentLogger LOGGER = AgentLogger.getLogger(IHotDeployServiceImpl.class);

    public IHotDeployServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String uploadResourceFile(String path, String content) throws RemoteException {
        LOGGER.info("上传资源文件");
        return null;
    }

    @Override
    public HotSwapResponse batchHotswapModifiedJava(List<BatchModifiedRequest> requestList) throws RemoteException {
        LOGGER.info("批量热更新修改的java");
        return null;
    }

}
