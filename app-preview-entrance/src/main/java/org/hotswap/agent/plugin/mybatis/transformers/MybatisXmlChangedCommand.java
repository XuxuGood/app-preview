package org.hotswap.agent.plugin.mybatis.transformers;

import org.hotswap.agent.command.MergeableCommand;
import org.hotswap.agent.command.Scheduler;
import org.hotswap.agent.logging.AgentLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

public class MybatisXmlChangedCommand extends MergeableCommand {
    private static AgentLogger LOGGER = AgentLogger.getLogger(MybatisXmlChangedCommand.class);

    ClassLoader appClassLoader;
    URL url;
    Scheduler scheduler;

    public MybatisXmlChangedCommand(ClassLoader appClassLoader, URL url, Scheduler scheduler) {
        this.appClassLoader = appClassLoader;
        this.url = url;
        this.scheduler = scheduler;
    }

    @Override
    public void executeCommand() {
        try {
            Class<?> clazz = Class.forName("org.hotswap.agent.plugin.mybatis.MyBatisRefreshCommands", true, appClassLoader);
            Method method = clazz.getDeclaredMethod(
                    "reloadConfiguration", URL.class);
            method.invoke(null, url);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Plugin error, method not found", e);
        } catch (InvocationTargetException e) {
            LOGGER.error("Error invoking method", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Plugin error, illegal access", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Plugin error, Spring class not found in application classloader", e);
        }
    }
}
