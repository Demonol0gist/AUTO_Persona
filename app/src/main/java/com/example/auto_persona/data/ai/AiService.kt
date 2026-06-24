package com.example.auto_persona.data.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class AiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun generatePersona(
        config: AiConfig,
        userInput: String,
        templateExample: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val systemPrompt = """你是一个二次元角色人设生成器。根据用户描述，生成一个完整的角色人设JSON。

你必须严格按以下JSON格式输出（只输出JSON，不要任何额外文字）：
{
  "name": "角色名（英文大写）",
  "description": "[姓名:XX][年龄:XX][性别:X][身份:XX][外貌:XX][性格:XX][喜好:XX][称呼:XX][对话:XX]",
  "personality": "核心性格描述，50字以内",
  "scenario": "场景设定：你是用户的XX，与用户的关系和互动模式",
  "creator_notes": "输出格式规则：[每次发言结尾加<好感变化:+X>,X∈[-5,5]][限制:≤100字]严格遵守输出格式：仅输出角色台词和标签，不要输出思考过程或补充说明。",
  "first_mes": "角色的开场白第一句话（可选，可为空）",
  "tags": ["标签1", "标签2", "标签3"]
}

参考模板示例：
$templateExample

用户要求：$userInput"""

            val body = buildString {
                append("{")
                append("\"model\":\"${config.model}\",")
                append("\"messages\":[")
                append("{\"role\":\"system\",\"content\":${escapeJson(systemPrompt)}},")
                append("{\"role\":\"user\",\"content\":${escapeJson(userInput)}}")
                append("],")
                append("\"temperature\":0.8,")
                append("\"max_tokens\":2048")
                append("}")
            }

            val request = Request.Builder()
                .url("${config.baseUrl}/v1/chat/completions")
                .addHeader("Authorization", "Bearer ${config.apiKey}")
                .addHeader("Content-Type", "application/json")
                .post(body.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            response.close()

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("API错误 ${response.code}: $responseBody"))
            }

            val jsonResponse = json.parseToJsonElement(responseBody).jsonObject
            val choices = jsonResponse["choices"]?.jsonArray
            val content = choices?.get(0)?.jsonObject?.get("message")?.jsonObject?.get("content")?.jsonPrimitive?.content
                ?: return@withContext Result.failure(Exception("解析AI响应失败"))

            // Extract JSON from response (AI might wrap in markdown code blocks)
            val jsonContent = extractJson(content)
            Result.success(jsonContent)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractJson(text: String): String {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1)
        }
        return text
    }

    private fun escapeJson(s: String): String {
        val sb = StringBuilder("\"")
        for (c in s) {
            when (c) {
                '"' -> sb.append("\\\"")
                '\\' -> sb.append("\\\\")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                '\b' -> sb.append("\\b")
                else -> sb.append(c)
            }
        }
        sb.append("\"")
        return sb.toString()
    }
}
