# 简介
核心热部署源码都在 app-preview-hotswap-agent 模块，此 app-preview-entrance 模块仅仅是作为启动器触发调用 app-preview-hotswap-agent 模块。

# app-preview-entrance 功能
1. Vert.x Web 服务
2. 调用 app-preview-hotswap-agent 核心热部署模块
3. 覆盖 app-preview-hotswap-agent 部分源码，实现扩展功能，详见 org.hotswap.agent.extension.IHotExtHandler 类。
4. 



