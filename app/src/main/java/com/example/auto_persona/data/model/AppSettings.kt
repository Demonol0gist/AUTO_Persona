package com.example.auto_persona.data.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class AppSettings(
    val bgmVolume: Int = 70,
    val voiceVolume: Int = 80,
    val isMuted: Boolean = false,
    val longTermMemory: Map<String, String> = emptyMap(),
    val ttsEnabled: Boolean = true,
    val presetRepliesEnabled: Boolean = true
)
