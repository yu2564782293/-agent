package com.solo.androidagent.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.solo.androidagent.agent.ChatAgent
import com.solo.androidagent.agent.GetDeviceInfoTool
import com.solo.androidagent.agent.GetTimeTool
import com.solo.androidagent.agent.ToolRegistry
import com.solo.androidagent.data.SettingsStore
import com.solo.androidagent.llm.ChatMessage
import com.solo.androidagent.llm.OpenAICompatibleClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val settings: UiSettings = UiSettings(),
    val messages: List<UiMessage> = emptyList(),
    val isSending: Boolean = false,
)

class ChatViewModel(
    private val settingsStore: SettingsStore,
    private val agent: ChatAgent,
) : ViewModel() {
    private val openAiHistory: MutableList<ChatMessage> = mutableListOf()

    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state

    init {
        viewModelScope.launch {
            settingsStore.settings.collect { settings ->
                _state.update { it.copy(settings = settings) }
            }
        }
    }

    fun send(text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return
        if (_state.value.isSending) return

        viewModelScope.launch {
            _state.update { it.copy(isSending = true) }
            try {
                val settings = _state.value.settings
                val result =
                    agent.run(
                        baseUrl = settings.baseUrl,
                        apiKey = settings.apiKey,
                        model = settings.model,
                        systemPrompt = settings.systemPrompt,
                        history = openAiHistory.toList(),
                        userInput = trimmed,
                    )

                openAiHistory.clear()
                openAiHistory.addAll(result.filter { it.role != "system" })
                _state.update {
                    it.copy(messages = openAiHistory.toUiMessages(), isSending = false)
                }
            } catch (t: Throwable) {
                _state.update {
                    it.copy(
                        messages =
                            it.messages +
                                UiMessage(
                                    role = UiRole.Error,
                                    content = t.message ?: t.toString(),
                                ),
                        isSending = false,
                    )
                }
            }
        }
    }

    fun saveSettings(settings: UiSettings) {
        viewModelScope.launch {
            settingsStore.save(settings)
        }
    }

    private fun List<ChatMessage>.toUiMessages(): List<UiMessage> =
        mapIndexed { index, msg ->
            val role =
                when (msg.role) {
                    "user" -> UiRole.User
                    "assistant" -> UiRole.Assistant
                    "tool" -> UiRole.Tool
                    "system" -> UiRole.System
                    else -> UiRole.Error
                }
            UiMessage(
                id = "$index-${msg.role}",
                role = role,
                content = msg.content.orEmpty(),
            )
        }

    class Factory(private val appContext: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val settingsStore = SettingsStore(appContext)
            val toolRegistry = ToolRegistry(listOf(GetTimeTool(), GetDeviceInfoTool()))
            val client = OpenAICompatibleClient()
            val agent = ChatAgent(client = client, toolRegistry = toolRegistry)
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(settingsStore = settingsStore, agent = agent) as T
        }
    }
}

