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
import com.example.auto_persona.ui.components.SliderRow
import com.example.auto_persona.ui.components.SwitchRow
import com.example.auto_persona.viewmodel.SaveEditorViewModel
import com.example.auto_persona.viewmodel.SaveState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSettingsScreen(
    navController: NavHostController,
    viewModel: SaveEditorViewModel
) {
    val saveState by viewModel.saveState.collectAsState()
    val state = saveState as? SaveState.Loaded ?: return
    val gs = state.saveData.data.gameData.settings

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("游戏设置") },
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
            SliderRow(
                label = "音乐音量",
                value = gs.musicVolume.toFloat(),
                onValueChange = { viewModel.updateMusicVolume(it.toDouble()) },
                valueRange = 0f..1f,
                displayValue = "%.1f".format(gs.musicVolume)
            )
            SliderRow(
                label = "音效音量",
                value = gs.soundVolume.toFloat(),
                onValueChange = { viewModel.updateSoundVolume(it.toDouble()) },
                valueRange = 0f..1f,
                displayValue = "%.1f".format(gs.soundVolume)
            )
            SwitchRow(
                label = "自动存档",
                checked = gs.autoSave,
                onCheckedChange = { viewModel.updateAutoSave(it) }
            )
        }
    }
}
