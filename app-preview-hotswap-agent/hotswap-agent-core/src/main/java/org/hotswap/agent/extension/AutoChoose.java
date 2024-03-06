package org.hotswap.agent.extension;

import org.hotswap.agent.extension.exception.RemoteItException;
import org.hotswap.agent.extension.manager.AllExtensionsManager;
import org.hotswap.agent.logging.AgentLogger;

import java.util.Iterator;

/**
 * 智能加载所需要的扩展类
 *
 * @Author xiaoxuxuy
 * @Date 2024年02月29日
 * @Version: 1.0
 */
public class AutoChoose implements IHotExtHandler {

    private static final AgentLogger logger = AgentLogger.getLogger(AutoChoose.class);

    @Override
    public byte[] preHandle(ClassLoader classLoader, String path, byte[] content) {
        Iterator<IHotExtHandler> handlerIterator = AllExtensionsManager.getInstance().getAllHandlers().iterator();

        while (handlerIterator.hasNext()) {
            IHotExtHandler hotExtHandler = handlerIterator.next();
            try {
                logger.info("preHandle {}", hotExtHandler);
                content = hotExtHandler.preHandle(classLoader, path, content);
            } catch (RemoteItException e) {
                logger.error("Remove handler {}", e, hotExtHandler);
                handlerIterator.remove();
            } catch (Throwable throwable) {
                logger.error("Error in preHandle {}", throwable);
            }
        }
        return content;
    }

    @Override
    public void afterHandle(ClassLoader classLoader, Class<?> classz, String path, byte[] content) {
        Iterator<IHotExtHandler> handlerIterator = AllExtensionsManager.getInstance().getAllHandlers().iterator();

        while (handlerIterator.hasNext()) {
            IHotExtHandler hotExtHandler = handlerIterator.next();
            try {
                logger.info("afterHandle {}", hotExtHandler);
                hotExtHandler.afterHandle(classLoader, classz, path, content);
            } catch (RemoteItException e) {
                logger.error("Remove handler {}", e, hotExtHandler);
                handlerIterator.remove();
            } catch (Throwable throwable) {
                logger.error("Error in afterHandle {}", throwable);
            }
        }

    }

}
