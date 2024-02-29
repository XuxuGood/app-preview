package com.netease.cloud.extension;

import com.netease.cloud.extension.exception.RemoteItException;
import com.netease.cloud.extension.logging.Logger;
import com.netease.cloud.extension.manager.AllExtensionsManager;

import java.util.Iterator;

/**
 * 在hotswap-remote.xml中配置这个类，智能加载所需要的扩展类
 *
 * @Author xiaoxuxuy
 * @Date 2024年02月29日
 * @Version: 1.0
 */
public class AutoChoose implements IHotExtHandler {

    private static final Logger logger = Logger.getLogger(AutoChoose.class);

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
