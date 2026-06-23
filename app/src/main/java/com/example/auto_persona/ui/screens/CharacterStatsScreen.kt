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
import com.example.auto_persona.data.model.CharacterStats
import com.example.auto_persona.data.model.CharacterSystemData
import com.example.auto_persona.ui.components.IntSliderRow
import com.example.auto_persona.ui.components.SectionHeader
import com.example.auto_persona.viewmodel.SaveEditorViewModel
import com.example.auto_persona.viewmodel.SaveState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterStatsScreen(
    navController: NavHostController,
    viewModel: SaveEditorViewModel
) {
    val saveState by viewModel.saveState.collectAsState()
    val stats by remember { derivedStateOf { (saveState as? SaveState.Loaded)?.saveData?.data?.gameData?.characterStats } }
    val sysData by remember { derivedStateOf { (saveState as? SaveState.Loaded)?.saveData?.data?.gameData?.characterSystemData } }
    if (stats == null || sysData == null) return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("角色属性") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IntSliderRow("好感度", stats!!.affection, { viewModel.updateAffection(it) }, 0..sysData!!.affectionCap)
            IntSliderRow("信任度", stats!!.trust, { viewModel.updateTrust(it) }, sysData!!.trustMin..sysData!!.trustMax)
            SectionHeader("角色信息")
            HorizontalDivider()
            Text("姓名: ${sysData!!.character.name}", style = MaterialTheme.typography.bodyLarge)
            Text("性格: ${sysData!!.character.personality}", style = MaterialTheme.typography.bodyLarge)
            Text("心情: ${sysData!!.character.currentMood}", style = MaterialTheme.typography.bodyLarge)
            Text("喜好礼物: ${sysData!!.character.favoriteGifts.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
            Text("特殊事件: ${sysData!!.character.specialEvents.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
            SectionHeader("数值限制")
            HorizontalDivider()
            Text("好感度上限: ${sysData!!.affectionCap}", style = MaterialTheme.typography.bodyMedium)
            Text("信任度范围: ${sysData!!.trustMin} ~ ${sysData!!.trustMax}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
