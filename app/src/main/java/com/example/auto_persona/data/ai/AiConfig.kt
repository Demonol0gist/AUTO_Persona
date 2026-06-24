package com.example.auto_persona.data.ai

import kotlinx.serialization.Serializable

@Serializable
data class AiConfig(
    val apiKey: String = "",
    val baseUrl: String = "https://api.deepseek.com",
    val model: String = "deepseek-v4-pro"
)
