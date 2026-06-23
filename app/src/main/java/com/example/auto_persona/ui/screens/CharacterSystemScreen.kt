package com.example.auto_persona.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.auto_persona.ui.components.*
import com.example.auto_persona.viewmodel.SaveEditorViewModel
import com.example.auto_persona.viewmodel.SaveState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterSystemScreen(
    navController: NavHostController,
    viewModel: SaveEditorViewModel
) {
    val saveState by viewModel.saveState.collectAsState()
    val state = saveState as? SaveState.Loaded ?: return
    val cs = state.saveData.data.gameData.characterSystemData

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("角色系统") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader("属性值")
            IntSliderRow("好感度", cs.stats.affection, { viewModel.updateSystemAffection(it) }, 0..cs.affectionCap)
            IntSliderRow("信任度", cs.stats.trust, { viewModel.updateSystemTrust(it) }, cs.trustMin..cs.trustMax)

            SectionHeader("角色信息")
            TextField(label = "姓名", value = cs.character.name, onValueChange = { viewModel.updateCharName(it) })
            TextField(label = "性格", value = cs.character.personality, onValueChange = { viewModel.updateCharPersonality(it) })
            TextField(label = "当前心情", value = cs.character.currentMood, onValueChange = { viewModel.updateCharMood(it) })
            ChipGroup(
                label = "喜好礼物",
                items = cs.character.favoriteGifts,
                onItemsChange = { viewModel.updateFavoriteGifts(it) }
            )
            ChipGroup(
                label = "特殊事件",
                items = cs.character.specialEvents,
                onItemsChange = { viewModel.updateSpecialEvents(it) }
            )

            SectionHeader("事件触发状态")
            SwitchRow("日记泄露已触发", cs.diaryLeakTriggered, { viewModel.updateDiaryLeakTriggered(it) })
            SwitchRow("病娇事件已触发", cs.yandereEventTriggered, { viewModel.updateYandereEventTriggered(it) })
            SwitchRow("礼物偏好事件已触发", cs.giftPreferenceEventTriggered, { viewModel.updateGiftPreferenceEventTriggered(it) })
            SwitchRow("好感30事件已触发", cs.affection30EventTriggered, { viewModel.updateAffection30EventTriggered(it) })
            SwitchRow("好感70事件已触发", cs.affection70EventTriggered, { viewModel.updateAffection70EventTriggered(it) })

            SectionHeader("数值上下限")
            NumberField("好感度上限", cs.affectionCap, { viewModel.updateAffectionCap(it) })
            NumberField("信任度下限", cs.trustMin, { viewModel.updateTrustMin(it) })
            NumberField("信任度上限", cs.trustMax, { viewModel.updateTrustMax(it) })

            SectionHeader("兽耳设定")
            IntSliderRow("害羞值", cs.kemonomimiShyness, { viewModel.updateKemonomimiShyness(it) }, 0..100)
            SwitchRow("猫形态", cs.kemonomimiCatForm, { viewModel.updateKemonomimiCatForm(it) })
            TextField(
                label = "当前人格包ID",
                value = cs.currentPersonaPackId ?: "",
                onValueChange = { viewModel.updateCurrentPersonaPackId(it.ifEmpty { null }) }
            )

            SectionHeader("恢复标记")
            SwitchRow("日记泄露已恢复", cs.diaryLeakRecovered, { viewModel.updateDiaryLeakRecovered(it) })
            SwitchRow("病娇已恢复", cs.yandereRecovered, { viewModel.updateYandereRecovered(it) })
            SwitchRow("服装19已解锁", cs.outfit19Unlocked, { viewModel.updateOutfit19Unlocked(it) })
        }
    }
}
