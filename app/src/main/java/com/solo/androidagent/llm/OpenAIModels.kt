package com.solo.androidagent.llm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val tools: List<ChatTool>? = null,
    @SerialName("tool_choice") val toolChoice: String? = null,
    val temperature: Double? = null,
)

@Serializable
data class ChatCompletionResponse(
    val id: String? = null,
    val choices: List<ChatChoice> = emptyList(),
)

@Serializable
data class ChatChoice(
    val index: Int,
    val message: ChatMessage,
    @SerialName("finish_reason") val finishReason: String? = null,
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String? = null,
    @SerialName("tool_calls") val toolCalls: List<ToolCall>? = null,
    @SerialName("tool_call_id") val toolCallId: String? = null,
)

@Serializable
data class ChatTool(
    val type: String = "function",
    val function: ChatToolFunction,
)

@Serializable
data class ChatToolFunction(
    val name: String,
    val description: String? = null,
    val parameters: JsonObject? = null,
)

@Serializable
data class ToolCall(
    val id: String,
    val type: String,
    val function: ToolCallFunction,
)

@Serializable
data class ToolCallFunction(
    val name: String,
    val arguments: String,
)

