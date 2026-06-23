package com.example.auto_persona.data.model

import kotlinx.serialization.Serializable

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
