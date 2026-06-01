package com.solo.androidagent.agent

import com.solo.androidagent.llm.ChatCompletionRequest
import com.solo.androidagent.llm.ChatMessage
import com.solo.androidagent.llm.OpenAICompatibleClient
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class ChatAgent(
    private val client: OpenAICompatibleClient,
    private val toolRegistry: ToolRegistry,
    private val json: Json = Json { ignoreUnknownKeys = true },
) {
    suspend fun run(
        baseUrl: String,
        apiKey: String,
        model: String,
        systemPrompt: String,
        history: List<ChatMessage>,
        userInput: String,
    ): List<ChatMessage> {
        val conversation =
            buildList {
                if (systemPrompt.isNotBlank()) {
                    add(ChatMessage(role = "system", content = systemPrompt))
                }
                addAll(history)
                add(ChatMessage(role = "user", content = userInput))
            }

        val maxTurns = 6
        var current = conversation

        repeat(maxTurns) {
            val response =
                client.createChatCompletion(
                    baseUrl = baseUrl,
                    apiKey = apiKey,
                    request =
                        ChatCompletionRequest(
                            model = model,
                            messages = current,
                            tools = toolRegistry.asChatTools(),
                        )
                )

            val assistant = response.choices.firstOrNull()?.message
                ?: throw IllegalStateException("No choices returned by model")

            val toolCalls = assistant.toolCalls.orEmpty()
            if (toolCalls.isEmpty()) {
                return current + assistant
            }

            val toolMessages =
                toolCalls.map { call ->
                    val tool = toolRegistry.find(call.function.name)
                        ?: throw IllegalStateException("Tool not found: ${call.function.name}")

                    val args = parseArguments(call.function.arguments)
                    val result = tool.call(args)

                    ChatMessage(
                        role = "tool",
                        toolCallId = call.id,
                        content = result,
                    )
                }

            current = current + assistant + toolMessages
            delay(100)
        }

        throw IllegalStateException("Tool calling did not converge within maxTurns")
    }

    private fun parseArguments(arguments: String): JsonObject {
        val trimmed = arguments.trim()
        if (trimmed.isBlank()) return JsonObject(emptyMap())
        val element = json.parseToJsonElement(trimmed)
        return element.jsonObject
    }
}

