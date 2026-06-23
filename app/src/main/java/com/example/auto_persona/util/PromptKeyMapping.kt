package com.example.auto_persona.util

val promptKeyDisplayNames = mapOf(
    "sister-null" to "默认 (病娇元意识)",
    "sister-verylow" to "极低好感 (毒舌冷淡)",
    "sister-low" to "低好感 (内向慢热)",
    "sister-medium" to "中等好感 (温柔试探)",
    "sister-high" to "高好感 (亲昵黏人)",
    "sister-dilei" to "地雷系 (情绪化占有)",
    "sister-kindergarten" to "幼儿园 (变小萝莉)",
    "sister-tutor" to "家教 (苏格拉底式)",
    "sister-kemonomimi" to "兽耳娘 (伶鼬形态)",
    "sister-kemonomimi-cat" to "伶鼬变身 (害羞过载)"
)

fun displayNameForPromptKey(key: String): String = promptKeyDisplayNames[key] ?: key

val allPromptKeys = listOf(
    "sister-null",
    "sister-verylow",
    "sister-low",
    "sister-medium",
    "sister-high",
    "sister-dilei",
    "sister-kindergarten",
    "sister-tutor",
    "sister-kemonomimi",
    "sister-kemonomimi-cat"
)
