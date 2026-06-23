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
    val state = saveState as? SaveState.Loaded ?: return
    val gd = state.saveData.data.gameData
    val cs = gd.characterSystemData

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("角色属性") },
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
            IntSliderRow(
                label = "好感度",
                value = gd.characterStats.affection,
                onValueChange = { viewModel.updateAffection(it) },
                valueRange = 0..cs.affectionCap
            )
            IntSliderRow(
                label = "信任度",
                value = gd.characterStats.trust,
                onValueChange = { viewModel.updateTrust(it) },
                valueRange = cs.trustMin..cs.trustMax
            )

            SectionHeader("角色信息")
            HorizontalDivider()
            Text("姓名: ${cs.character.name}", style = MaterialTheme.typography.bodyLarge)
            Text("性格: ${cs.character.personality}", style = MaterialTheme.typography.bodyLarge)
            Text("心情: ${cs.character.currentMood}", style = MaterialTheme.typography.bodyLarge)
            Text("喜好礼物: ${cs.character.favoriteGifts.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
            Text("特殊事件: ${cs.character.specialEvents.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)

            SectionHeader("数值限制")
            HorizontalDivider()
            Text("好感度上限: ${cs.affectionCap}", style = MaterialTheme.typography.bodyMedium)
            Text("信任度范围: ${cs.trustMin} ~ ${cs.trustMax}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
