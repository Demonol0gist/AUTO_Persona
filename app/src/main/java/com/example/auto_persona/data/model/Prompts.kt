package com.example.auto_persona.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PromptsMap(
    @SerialName("sister-null") val sisterNull: PromptData? = null,
    @SerialName("sister-verylow") val sisterVerylow: PromptData? = null,
    @SerialName("sister-low") val sisterLow: PromptData? = null,
    @SerialName("sister-medium") val sisterMedium: PromptData? = null,
    @SerialName("sister-high") val sisterHigh: PromptData? = null,
    @SerialName("sister-dilei") val sisterDilei: PromptData? = null,
    @SerialName("sister-kindergarten") val sisterKindergarten: PromptData? = null,
    @SerialName("sister-tutor") val sisterTutor: PromptData? = null,
    @SerialName("sister-kemonomimi") val sisterKemonomimi: PromptData? = null,
    @SerialName("sister-kemonomimi-cat") val sisterKemonomimiCat: PromptData? = null
)

@Serializable
data class PromptData(
    val spec: String = "chara_card_v2",
    @SerialName("spec_version") val specVersion: String = "2.0",
    val data: PromptInnerData = PromptInnerData()
)

@Serializable
data class PromptInnerData(
    val name: String = "Yuki",
    val description: String = "",
    val personality: String = "",
    val scenario: String = "",
    @SerialName("creator_notes") val creatorNotes: String = "",
    @SerialName("first_mes") val firstMes: String? = null,
    val tags: List<String> = emptyList()
)
