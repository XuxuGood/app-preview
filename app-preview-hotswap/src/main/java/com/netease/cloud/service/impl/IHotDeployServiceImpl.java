package com.netease.cloud.service.impl;

import com.netease.cloud.extension.AutoChoose;
import com.netease.cloud.model.BatchModifiedRequest;
import com.netease.cloud.model.HotSwapResponse;
import com.netease.cloud.service.IHotDeployService;
import org.hotswap.agent.config.PluginManager;
import org.hotswap.agent.javassist.*;
import org.hotswap.agent.logging.AgentLogger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author xiaoxuxuy
 * @Date 2024年02月29日
 * @Version: 1.0
 */
public class IHotDeployServiceImpl extends UnicastRemoteObject implements IHotDeployService {

    private static final AgentLogger LOGGER = AgentLogger.getLogger(IHotDeployServiceImpl.class);

    private final AutoChoose autoChoose;

    public IHotDeployServiceImpl() throws Exception {
        super();
        autoChoose = new AutoChoose();
    }

    @Override
    public HotSwapResponse uploadResourceFile(String path, String content) throws RemoteException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        // 前置处理
        autoChoose.preHandle(contextClassLoader, path, content.getBytes());

        // 将content内容写进path文件中
        try (FileOutputStream fos = new FileOutputStream(path)) {
            // 将content内容写入到文件中
            fos.write(content.getBytes());
            fos.flush();
        } catch (IOException e) {
            LOGGER.error("Exception writing to file：" + e.getMessage(), e);
            return HotSwapResponse.errorOf("Exception writing to file", e.getMessage());
        }

        // 后置处理
        autoChoose.afterHandle(contextClassLoader, null, path, content.getBytes());

        return HotSwapResponse.success("Resource updated successfully");
    }

    @Override
    public HotSwapResponse batchHotswapModifiedJava(List<BatchModifiedRequest> requestList) throws RemoteException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        Map<Class<?>, byte[]> reloadMap = new HashMap<>();
        requestList.forEach(request -> {
            try {
                String className = request.getClassName();
                Class<?> aClass = Class.forName(className);
                // 前置处理
                autoChoose.preHandle(contextClassLoader, className, request.getBytes());

                // 后置处理
                autoChoose.afterHandle(contextClassLoader, aClass, className, request.getBytes());

                ClassPool classPool = new ClassPool();
                classPool.appendClassPath(new LoaderClassPath(aClass.getClassLoader()));

                CtClass ctClass = classPool.getAndRename(aClass.getName(), aClass.getName());
                reloadMap.put(aClass, ctClass.toBytecode());
            } catch (ClassNotFoundException | NotFoundException | IOException | CannotCompileException e) {
                throw new RuntimeException(e);
            }
        });

        PluginManager.getInstance().hotswap(reloadMap);

        return HotSwapResponse.success("success, updates(include inner classes)=" + requestList.size());
    }

}
