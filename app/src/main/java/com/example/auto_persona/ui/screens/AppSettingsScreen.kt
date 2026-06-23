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
    val appSets by remember { derivedStateOf { (saveState as? SaveState.Loaded)?.saveData?.data?.settings } }
    if (appSets == null) return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("应用设置") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IntSliderRow("背景音乐音量", appSets!!.bgmVolume, { viewModel.updateBgmVolume(it) }, 0..100)
            IntSliderRow("语音音量", appSets!!.voiceVolume, { viewModel.updateVoiceVolume(it) }, 0..100)
            SwitchRow("静音", appSets!!.isMuted, { viewModel.updateIsMuted(it) })
            SwitchRow("TTS已启用", appSets!!.ttsEnabled, { viewModel.updateTtsEnabled(it) })
            SwitchRow("预设回复已启用", appSets!!.presetRepliesEnabled, { viewModel.updatePresetRepliesEnabled(it) })
        }
    }
}
