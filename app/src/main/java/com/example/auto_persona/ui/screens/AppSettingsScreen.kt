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
import com.example.auto_persona.ui.components.SwitchRow
import com.example.auto_persona.viewmodel.SaveEditorViewModel
import com.example.auto_persona.viewmodel.SaveState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(
    navController: NavHostController,
    viewModel: SaveEditorViewModel
) {
    val saveState by viewModel.saveState.collectAsState()
    val state = saveState as? SaveState.Loaded ?: return
    val appSets = state.saveData.data.settings

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("应用设置") },
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
                label = "背景音乐音量",
                value = appSets.bgmVolume,
                onValueChange = { viewModel.updateBgmVolume(it) },
                valueRange = 0..100
            )
            IntSliderRow(
                label = "语音音量",
                value = appSets.voiceVolume,
                onValueChange = { viewModel.updateVoiceVolume(it) },
                valueRange = 0..100
            )
            SwitchRow(label = "静音", checked = appSets.isMuted, onCheckedChange = { viewModel.updateIsMuted(it) })
            SwitchRow(label = "TTS已启用", checked = appSets.ttsEnabled, onCheckedChange = { viewModel.updateTtsEnabled(it) })
            SwitchRow(label = "预设回复已启用", checked = appSets.presetRepliesEnabled, onCheckedChange = { viewModel.updatePresetRepliesEnabled(it) })
        }
    }
}
