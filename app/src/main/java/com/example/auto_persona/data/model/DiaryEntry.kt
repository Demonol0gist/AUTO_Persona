package com.example.auto_persona.data.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class DiaryEntry(
    val timestamp: String = "",
    val date: String = "",
    val time: String = "",
    val affection: Int = 0,
    val content: String = "",
    val mode: String = "",
    val diaryId: String = ""
)
