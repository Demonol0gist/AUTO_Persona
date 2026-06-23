package com.example.auto_persona.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GameData(
    val version: String = "1.0.0",
    val timestamp: Long = 0L,
    val playerInfo: PlayerInfo = PlayerInfo(),
    val gender: String = "brother",
    val characterStats: CharacterStats = CharacterStats(),
    val characterSystemData: CharacterSystemData = CharacterSystemData(),
    val playerProgress: PlayerProgress = PlayerProgress(),
    val questSystem: QuestSystem = QuestSystem(),
    val globalLevelSystem: GlobalLevelSystem = GlobalLevelSystem(),
    val timeSystem: TimeSystem = TimeSystem(),
    val inventory: Inventory = Inventory(),
    val settings: GameSettings = GameSettings(),
    val dateHistory: DateHistory = DateHistory(),
    val travelSystem: TravelSystem = TravelSystem(),
    val shopSystem: ShopSystem = ShopSystem(),
    val currentOutfit: String? = null,
    val kemonomimiReminderCount: Int = 0
)

@Serializable
data class PlayerInfo(
    val name: String = "",
    val birthday: String = "",
    val identity: String = "",
    val hasCollected: Boolean = false
)

@Serializable
data class CharacterStats(
    val affection: Int = 0,
    val trust: Int = 0
)

@Serializable
data class CharacterSystemData(
    val stats: CharacterStats = CharacterStats(),
    val character: CharacterInfo = CharacterInfo(),
    val diaryLeakTriggered: Boolean = false,
    val yandereEventTriggered: Boolean = false,
    val giftPreferenceEventTriggered: Boolean = false,
    val affection30EventTriggered: Boolean = false,
    val affection70EventTriggered: Boolean = false,
    val affectionCap: Int = 1000,
    val trustMin: Int = 0,
    val trustMax: Int = 100,
    val kemonomimiShyness: Int = 0,
    val kemonomimiCatForm: Boolean = false,
    val currentPersonaPackId: String? = null,
    val diaryLeakRecovered: Boolean = false,
    val yandereRecovered: Boolean = false,
    val outfit19Unlocked: Boolean = false
)

@Serializable
data class CharacterInfo(
    val name: String = "YUKI",
    val personality: String = "",
    val favoriteGifts: List<String> = emptyList(),
    val specialEvents: List<String> = emptyList(),
    val currentMood: String = "normal"
)

@Serializable
data class PlayerProgress(
    val coins: Int = 0
)

@Serializable
data class QuestSystem(
    val unlockedLevel: Int = 0
)

@Serializable
data class GlobalLevelSystem(
    val globalLevel: Int = 1,
    val globalExp: Int = 0
)

@Serializable
data class TimeSystem(
    val currentDay: Int = 1,
    val dailyActions: Int = 99999,
    val actionsUsed: Int = 0,
    val specialEvents: List<String> = emptyList(),
    val gameStartDate: String = ""
)

@Serializable
data class Inventory(
    val items: List<InventoryItem> = emptyList(),
    val equipment: Equipment = Equipment()
)

@Serializable
data class InventoryItem(
    val id: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val type: String = ""
)

@Serializable
data class Equipment(
    val weapon: String? = null,
    val armor: String? = null,
    val accessory: String? = null
)

@Serializable
data class GameSettings(
    val musicVolume: Double = 0.7,
    val soundVolume: Double = 0.8,
    val autoSave: Boolean = true
)

@Serializable
data class DateHistory(
    val custom: Int = 0,
    val cafe: Int = 0,
    val park: Int = 0,
    val restaurant: Int = 0,
    val beach: Int = 0,
    val nightmarket: Int = 0,
    val nightpath: Int = 0
)

@Serializable
data class TravelSystem(
    val hasShownNightDialogue: Boolean = false,
    val travelHistory: Map<String, String> = emptyMap(),
    val unlockedDestinations: List<String> = emptyList(),
    val currentSpots: List<String> = emptyList(),
    val travelState: String? = null,
    val currentDestination: String? = null,
    val destinationCityName: String? = null,
    val currentCity: String? = null,
    val isTravelMode: Boolean = false,
    val travelStartTime: String? = null,
    val travelEndTime: String? = null
)

@Serializable
data class ShopSystem(
    val purchases: OutfitPurchases = OutfitPurchases(),
    val giftInventory: GiftInventory = GiftInventory(),
    val travelInventory: Map<String, Int> = emptyMap(),
    val currentTab: String = "travel"
)

@Serializable
data class OutfitPurchases(
    val outfit1: Boolean = false, val outfit2: Boolean = false,
    val outfit3: Boolean = false, val outfit4: Boolean = false,
    val outfit5: Boolean = false, val outfit6: Boolean = false,
    val outfit7: Boolean = false, val outfit8: Boolean = false,
    val outfit9: Boolean = false, val outfit10: Boolean = false,
    val outfit11: Boolean = false, val outfit12: Boolean = false,
    val outfit13: Boolean = false, val outfit14: Boolean = false,
    val outfit15: Boolean = false, val outfit16: Boolean = false,
    val outfit17: Boolean = false, val outfit18: Boolean = false,
    val outfit19: Boolean = false, val outfit20: Boolean = false
)

@Serializable
data class GiftInventory(
    val gift1: Int = 0, val gift2: Int = 0, val gift3: Int = 0,
    val gift4: Int = 0, val gift5: Int = 0, val gift6: Int = 0,
    val gift7: Int = 0, val gift8: Int = 0, val gift9: Int = 0,
    val gift10: Int = 0, val gift11: Int = 0, val gift12: Int = 0
)
