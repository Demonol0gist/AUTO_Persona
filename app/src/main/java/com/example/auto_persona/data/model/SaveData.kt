package com.example.auto_persona.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SaveData(
    val version: String = "1.0.0",
    val timestamp: Long = 0L,
    val exportDate: String = "",
    val slotId: Int = 1,
    val data: GameContainer = GameContainer()
)

@Serializable
data class GameContainer(
    val gameData: GameData = GameData(),
    val diary: List<DiaryEntry> = emptyList(),
    val settings: AppSettings = AppSettings(),
    val prompts: PromptsMap = PromptsMap()
)
