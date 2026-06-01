package com.solo.androidagent.ui

import java.util.UUID

enum class UiRole {
    System,
    User,
    Assistant,
    Tool,
    Error,
}

data class UiMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: UiRole,
    val content: String,
)

data class UiSettings(
    val baseUrl: String = "https://api.openai.com",
    val apiKey: String = "",
    val model: String = "gpt-4o-mini",
    val systemPrompt: String = "You are a helpful assistant.",
)

