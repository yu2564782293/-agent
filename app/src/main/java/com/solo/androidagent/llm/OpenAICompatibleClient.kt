package com.solo.androidagent.llm

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class OpenAICompatibleClient(
    private val httpClient: OkHttpClient = OkHttpClient(),
    private val json: Json = Json { ignoreUnknownKeys = true },
) {
    suspend fun createChatCompletion(
        baseUrl: String,
        apiKey: String,
        request: ChatCompletionRequest,
    ): ChatCompletionResponse {
        return withContext(Dispatchers.IO) {
            val normalizedBaseUrl = baseUrl.trim().removeSuffix("/")
            val url = "$normalizedBaseUrl/v1/chat/completions"
            val bodyString = json.encodeToString(ChatCompletionRequest.serializer(), request)
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = bodyString.toRequestBody(mediaType)

            val httpRequest =
                Request.Builder()
                    .url(url)
                    .post(body)
                    .apply {
                        if (apiKey.isNotBlank()) {
                            header("Authorization", "Bearer $apiKey")
                        }
                        header("Content-Type", "application/json")
                    }
                    .build()

            val call = httpClient.newCall(httpRequest)
            val response = call.execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful) {
                throw IllegalStateException("HTTP ${response.code}: ${responseBody ?: ""}".trim())
            }

            if (responseBody.isNullOrBlank()) {
                throw IllegalStateException("Empty response body")
            }

            json.decodeFromString(ChatCompletionResponse.serializer(), responseBody)
        }
    }
}
