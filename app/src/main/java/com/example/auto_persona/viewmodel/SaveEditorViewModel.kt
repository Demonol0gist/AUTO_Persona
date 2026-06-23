package com.example.auto_persona.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auto_persona.data.model.*
import com.example.auto_persona.data.repository.SaveRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class SaveState {
    object Loading : SaveState()
    data class Loaded(val saveData: SaveData, val source: String) : SaveState()
    data class Error(val message: String) : SaveState()
}

class SaveEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SaveRepository()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Loading)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        loadTemplate()
    }

    fun loadTemplate() {
        viewModelScope.launch {
            _saveState.value = SaveState.Loading
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.loadFromAssets(getApplication())
                }
                result.fold(
                    onSuccess = { _saveState.value = SaveState.Loaded(it, "模板") },
                    onFailure = { _saveState.value = SaveState.Error("加载模板失败: ${it.message}") }
                )
            } catch (e: Exception) {
                _saveState.value = SaveState.Error("加载模板失败: ${e.message}")
            }
        }
    }

    fun importSave(uri: Uri) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.importFromUri(getApplication(), uri)
                }
                result.fold(
                    onSuccess = { _saveState.value = SaveState.Loaded(it, "已导入") },
                    onFailure = { _snackbarMessage.value = "导入失败: ${it.message}" }
                )
            } catch (e: Exception) {
                _snackbarMessage.value = "导入失败: ${e.message}"
            }
        }
    }

    fun exportSave(uri: Uri) {
        val current = _saveState.value
        if (current !is SaveState.Loaded) {
            _snackbarMessage.value = "没有可导出的存档数据"
            return
        }
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.exportToUri(getApplication(), uri, current.saveData)
                }
                result.fold(
                    onSuccess = { _snackbarMessage.value = "导出成功" },
                    onFailure = { _snackbarMessage.value = "导出失败: ${it.message}" }
                )
            } catch (e: Exception) {
                _snackbarMessage.value = "导出失败: ${e.message}"
            }
        }
    }

    fun getJsonString(): String? {
        val current = _saveState.value
        return if (current is SaveState.Loaded) repository.toPrettyJson(current.saveData) else null
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    // region Player Info updates
    fun updatePlayerName(name: String) { updateGameData { it.copy(playerInfo = it.playerInfo.copy(name = name)) } }
    fun updatePlayerBirthday(birthday: String) { updateGameData { it.copy(playerInfo = it.playerInfo.copy(birthday = birthday)) } }
    fun updatePlayerIdentity(identity: String) { updateGameData { it.copy(playerInfo = it.playerInfo.copy(identity = identity)) } }
    fun updatePlayerHasCollected(hasCollected: Boolean) { updateGameData { it.copy(playerInfo = it.playerInfo.copy(hasCollected = hasCollected)) } }
    // endregion

    // region Gender
    fun updateGender(gender: String) { updateGameData { it.copy(gender = gender) } }
    // endregion

    // region Character Stats
    fun updateAffection(value: Int) { updateGameData { it.copy(characterStats = it.characterStats.copy(affection = value)) } }
    fun updateTrust(value: Int) { updateGameData { it.copy(characterStats = it.characterStats.copy(trust = value)) } }
    // endregion

    // region Character System Data
    fun updateCharName(name: String) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(character = it.characterSystemData.character.copy(name = name))) } }
    fun updateCharPersonality(personality: String) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(character = it.characterSystemData.character.copy(personality = personality))) } }
    fun updateCharMood(mood: String) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(character = it.characterSystemData.character.copy(currentMood = mood))) } }
    fun updateFavoriteGifts(gifts: List<String>) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(character = it.characterSystemData.character.copy(favoriteGifts = gifts))) } }
    fun updateSpecialEvents(events: List<String>) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(character = it.characterSystemData.character.copy(specialEvents = events))) } }
    fun updateDiaryLeakTriggered(v: Boolean) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(diaryLeakTriggered = v)) } }
    fun updateYandereEventTriggered(v: Boolean) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(yandereEventTriggered = v)) } }
    fun updateGiftPreferenceEventTriggered(v: Boolean) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(giftPreferenceEventTriggered = v)) } }
    fun updateAffection30EventTriggered(v: Boolean) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(affection30EventTriggered = v)) } }
    fun updateAffection70EventTriggered(v: Boolean) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(affection70EventTriggered = v)) } }
    fun updateAffectionCap(v: Int) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(affectionCap = v)) } }
    fun updateTrustMin(v: Int) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(trustMin = v)) } }
    fun updateTrustMax(v: Int) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(trustMax = v)) } }
    fun updateKemonomimiShyness(v: Int) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(kemonomimiShyness = v)) } }
    fun updateKemonomimiCatForm(v: Boolean) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(kemonomimiCatForm = v)) } }
    fun updateCurrentPersonaPackId(v: String?) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(currentPersonaPackId = v)) } }
    fun updateDiaryLeakRecovered(v: Boolean) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(diaryLeakRecovered = v)) } }
    fun updateYandereRecovered(v: Boolean) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(yandereRecovered = v)) } }
    fun updateOutfit19Unlocked(v: Boolean) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(outfit19Unlocked = v)) } }
    fun updateSystemAffection(v: Int) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(stats = it.characterSystemData.stats.copy(affection = v))) } }
    fun updateSystemTrust(v: Int) { updateGameData { it.copy(characterSystemData = it.characterSystemData.copy(stats = it.characterSystemData.stats.copy(trust = v))) } }
    // endregion

    // region Player Progress
    fun updateCoins(v: Int) { updateGameData { it.copy(playerProgress = it.playerProgress.copy(coins = v)) } }
    // endregion

    // region Quest
    fun updateUnlockedLevel(v: Int) { updateGameData { it.copy(questSystem = it.questSystem.copy(unlockedLevel = v)) } }
    // endregion

    // region Global Level
    fun updateGlobalLevel(v: Int) { updateGameData { it.copy(globalLevelSystem = it.globalLevelSystem.copy(globalLevel = v)) } }
    fun updateGlobalExp(v: Int) { updateGameData { it.copy(globalLevelSystem = it.globalLevelSystem.copy(globalExp = v)) } }
    // endregion

    // region Time System
    fun updateCurrentDay(v: Int) { updateGameData { it.copy(timeSystem = it.timeSystem.copy(currentDay = v)) } }
    fun updateDailyActions(v: Int) { updateGameData { it.copy(timeSystem = it.timeSystem.copy(dailyActions = v)) } }
    fun updateActionsUsed(v: Int) { updateGameData { it.copy(timeSystem = it.timeSystem.copy(actionsUsed = v)) } }
    fun updateGameStartDate(v: String) { updateGameData { it.copy(timeSystem = it.timeSystem.copy(gameStartDate = v)) } }
    fun updateTimeSpecialEvents(v: List<String>) { updateGameData { it.copy(timeSystem = it.timeSystem.copy(specialEvents = v)) } }
    // endregion

    // region Game Settings
    fun updateMusicVolume(v: Double) { updateGameData { it.copy(settings = it.settings.copy(musicVolume = v)) } }
    fun updateSoundVolume(v: Double) { updateGameData { it.copy(settings = it.settings.copy(soundVolume = v)) } }
    fun updateAutoSave(v: Boolean) { updateGameData { it.copy(settings = it.settings.copy(autoSave = v)) } }
    // endregion

    // region Date History
    fun updateDateCustom(v: Int) { updateGameData { it.copy(dateHistory = it.dateHistory.copy(custom = v)) } }
    fun updateDateCafe(v: Int) { updateGameData { it.copy(dateHistory = it.dateHistory.copy(cafe = v)) } }
    fun updateDatePark(v: Int) { updateGameData { it.copy(dateHistory = it.dateHistory.copy(park = v)) } }
    fun updateDateRestaurant(v: Int) { updateGameData { it.copy(dateHistory = it.dateHistory.copy(restaurant = v)) } }
    fun updateDateBeach(v: Int) { updateGameData { it.copy(dateHistory = it.dateHistory.copy(beach = v)) } }
    fun updateDateNightmarket(v: Int) { updateGameData { it.copy(dateHistory = it.dateHistory.copy(nightmarket = v)) } }
    fun updateDateNightpath(v: Int) { updateGameData { it.copy(dateHistory = it.dateHistory.copy(nightpath = v)) } }
    // endregion

    // region Travel System
    fun updateTravelMode(v: Boolean) { updateGameData { it.copy(travelSystem = it.travelSystem.copy(isTravelMode = v)) } }
    fun updateHasShownNightDialogue(v: Boolean) { updateGameData { it.copy(travelSystem = it.travelSystem.copy(hasShownNightDialogue = v)) } }
    fun updateCurrentDestination(v: String?) { updateGameData { it.copy(travelSystem = it.travelSystem.copy(currentDestination = v)) } }
    fun updateDestinationCityName(v: String?) { updateGameData { it.copy(travelSystem = it.travelSystem.copy(destinationCityName = v)) } }
    fun updateCurrentCity(v: String?) { updateGameData { it.copy(travelSystem = it.travelSystem.copy(currentCity = v)) } }
    fun updateTravelState(v: String?) { updateGameData { it.copy(travelSystem = it.travelSystem.copy(travelState = v)) } }
    fun updateTravelStartTime(v: String?) { updateGameData { it.copy(travelSystem = it.travelSystem.copy(travelStartTime = v)) } }
    fun updateTravelEndTime(v: String?) { updateGameData { it.copy(travelSystem = it.travelSystem.copy(travelEndTime = v)) } }
    fun updateUnlockedDestinations(v: List<String>) { updateGameData { it.copy(travelSystem = it.travelSystem.copy(unlockedDestinations = v)) } }
    fun updateCurrentSpots(v: List<String>) { updateGameData { it.copy(travelSystem = it.travelSystem.copy(currentSpots = v)) } }
    // endregion

    // region Inventory
    fun updateItems(items: List<InventoryItem>) { updateGameData { it.copy(inventory = it.inventory.copy(items = items)) } }
    fun updateWeapon(v: String?) { updateGameData { it.copy(inventory = it.inventory.copy(equipment = it.inventory.equipment.copy(weapon = v))) } }
    fun updateArmor(v: String?) { updateGameData { it.copy(inventory = it.inventory.copy(equipment = it.inventory.equipment.copy(armor = v))) } }
    fun updateAccessory(v: String?) { updateGameData { it.copy(inventory = it.inventory.copy(equipment = it.inventory.equipment.copy(accessory = v))) } }
    // endregion

    // region Shop
    fun updateOutfitPurchases(purchases: OutfitPurchases) { updateGameData { it.copy(shopSystem = it.shopSystem.copy(purchases = purchases)) } }
    fun updateGiftInventory(gifts: GiftInventory) { updateGameData { it.copy(shopSystem = it.shopSystem.copy(giftInventory = gifts)) } }
    fun updateTravelInventory(inv: Map<String, Int>) { updateGameData { it.copy(shopSystem = it.shopSystem.copy(travelInventory = inv)) } }
    fun updateShopTab(v: String) { updateGameData { it.copy(shopSystem = it.shopSystem.copy(currentTab = v)) } }
    // endregion

    // region Outfit
    fun updateCurrentOutfit(v: String?) { updateGameData { it.copy(currentOutfit = v) } }
    fun updateKemonomimiReminderCount(v: Int) { updateGameData { it.copy(kemonomimiReminderCount = v) } }
    // endregion

    // region App Settings
    fun updateBgmVolume(v: Int) { updateContainer { it.copy(settings = it.settings.copy(bgmVolume = v)) } }
    fun updateVoiceVolume(v: Int) { updateContainer { it.copy(settings = it.settings.copy(voiceVolume = v)) } }
    fun updateIsMuted(v: Boolean) { updateContainer { it.copy(settings = it.settings.copy(isMuted = v)) } }
    fun updateTtsEnabled(v: Boolean) { updateContainer { it.copy(settings = it.settings.copy(ttsEnabled = v)) } }
    fun updatePresetRepliesEnabled(v: Boolean) { updateContainer { it.copy(settings = it.settings.copy(presetRepliesEnabled = v)) } }
    // endregion

    // region Diary
    fun updateDiaryEntries(entries: List<DiaryEntry>) { updateContainer { it.copy(diary = entries) } }
    fun updateDiaryEntry(index: Int, entry: DiaryEntry) {
        updateContainer { container ->
            val mutableDiary = container.diary.toMutableList()
            if (index in mutableDiary.indices) {
                mutableDiary[index] = entry
            }
            container.copy(diary = mutableDiary)
        }
    }
    fun addDiaryEntry(entry: DiaryEntry) { updateContainer { it.copy(diary = it.diary + entry) } }
    fun removeDiaryEntry(index: Int) {
        updateContainer { container ->
            val mutableDiary = container.diary.toMutableList()
            if (index in mutableDiary.indices) {
                mutableDiary.removeAt(index)
            }
            container.copy(diary = mutableDiary)
        }
    }
    // endregion

    // region Prompts
    fun updatePrompt(key: String, prompt: PromptData?) {
        updateContainer { container ->
            val newPrompts = when (key) {
                "sister-null" -> container.prompts.copy(sisterNull = prompt)
                "sister-verylow" -> container.prompts.copy(sisterVerylow = prompt)
                "sister-low" -> container.prompts.copy(sisterLow = prompt)
                "sister-medium" -> container.prompts.copy(sisterMedium = prompt)
                "sister-high" -> container.prompts.copy(sisterHigh = prompt)
                "sister-dilei" -> container.prompts.copy(sisterDilei = prompt)
                "sister-kindergarten" -> container.prompts.copy(sisterKindergarten = prompt)
                "sister-tutor" -> container.prompts.copy(sisterTutor = prompt)
                "sister-kemonomimi" -> container.prompts.copy(sisterKemonomimi = prompt)
                "sister-kemonomimi-cat" -> container.prompts.copy(sisterKemonomimiCat = prompt)
                else -> container.prompts
            }
            container.copy(prompts = newPrompts)
        }
    }
    // endregion

    private inline fun updateGameData(crossinline block: (GameData) -> GameData) {
        updateContainer { container ->
            container.copy(gameData = block(container.gameData))
        }
    }

    private inline fun updateContainer(crossinline block: (GameContainer) -> GameContainer) {
        _saveState.update { current ->
            if (current is SaveState.Loaded) {
                current.copy(saveData = current.saveData.copy(data = block(current.saveData.data)))
            } else current
        }
    }
}
