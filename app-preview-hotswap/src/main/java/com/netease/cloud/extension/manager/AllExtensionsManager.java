package com.netease.cloud.extension.manager;

import com.netease.cloud.extension.IHotExtHandler;
import org.hotswap.agent.logging.AgentLogger;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 所有的扩展包注册管理器
 * @author Liubsyy
 */
public class AllExtensionsManager {
    private static final AgentLogger logger = AgentLogger.getLogger(AllExtensionsManager.class);

    private Map<Class<?>, IHotExtHandler> allHandlers = new ConcurrentHashMap<>();

    private static AllExtensionsManager instance = new AllExtensionsManager();

    static {
        //如果某个handler 100%要注册，写在这
        //instance.addHotExtHandler(new com.netease.cloud.extension.cache.VelocityHtmlCacheClear());
    }

    public static AllExtensionsManager getInstance() {
        return instance;
    }

    public void addHotExtHandler(IHotExtHandler hotExtHandler) {
        if(!allHandlers.containsKey(hotExtHandler.getClass())) {
            allHandlers.put(hotExtHandler.getClass(),hotExtHandler);
            logger.info("Register handler {}",hotExtHandler);
        }
    }

    public IHotExtHandler removeHotExtHandler(Class<?> classz) {
        IHotExtHandler oldHandler = allHandlers.remove(classz);
        if(null != oldHandler) {
            logger.info("Remove handler {}",oldHandler);
        }
        return oldHandler;
    }

    public Collection<IHotExtHandler> getAllHandlers(){
        return allHandlers.values();
    }


}
