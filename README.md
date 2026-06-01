# android-agent

一个最小可运行的 Android 版 LLM Agent（OpenAI 兼容 API），包含：

- 可配置 Base URL / API Key / Model / System Prompt
- 多轮聊天
- 工具调用（内置 `get_time`、`get_device_info`）

## 运行

- 用 Android Studio 打开仓库根目录
- 在 Settings 页面填入你的大模型服务配置
- 回到 Chat 页面开始对话

## 构建（命令行）

- 需要本机已安装 Android SDK，并配置 `ANDROID_HOME`，或由 Android Studio 自动生成 `local.properties`

```bash
./gradlew :app:assembleDebug
```
