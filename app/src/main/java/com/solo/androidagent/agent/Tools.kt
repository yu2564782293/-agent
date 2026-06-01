package com.solo.androidagent.agent

import android.os.Build
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

interface AgentTool {
    val name: String
    val description: String
    val parameters: JsonObject
    suspend fun call(args: JsonObject): String
}

class ToolRegistry(private val tools: List<AgentTool>) {
    fun asChatTools(): List<com.solo.androidagent.llm.ChatTool> =
        tools.map {
            com.solo.androidagent.llm.ChatTool(
                function =
                    com.solo.androidagent.llm.ChatToolFunction(
                        name = it.name,
                        description = it.description,
                        parameters = it.parameters,
                    )
            )
        }

    fun find(name: String): AgentTool? = tools.firstOrNull { it.name == name }
}

class GetTimeTool : AgentTool {
    override val name: String = "get_time"
    override val description: String = "Get current local time in ISO-8601 format"
    override val parameters: JsonObject = buildJsonObject { }

    override suspend fun call(args: JsonObject): String {
        val formatter =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault())
        return formatter.format(Instant.now())
    }
}

class GetDeviceInfoTool : AgentTool {
    override val name: String = "get_device_info"
    override val description: String = "Get basic Android device information"
    override val parameters: JsonObject = buildJsonObject { }

    override suspend fun call(args: JsonObject): String =
        buildJsonObject {
            put("brand", Build.BRAND)
            put("manufacturer", Build.MANUFACTURER)
            put("model", Build.MODEL)
            put("sdkInt", Build.VERSION.SDK_INT)
            put("release", Build.VERSION.RELEASE)
        }.toString()
}

