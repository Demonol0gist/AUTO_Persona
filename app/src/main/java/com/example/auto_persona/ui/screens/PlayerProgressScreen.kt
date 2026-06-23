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
import com.example.auto_persona.ui.components.NumberField
import com.example.auto_persona.viewmodel.SaveEditorViewModel
import com.example.auto_persona.viewmodel.SaveState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProgressScreen(
    navController: NavHostController,
    viewModel: SaveEditorViewModel
) {
    val saveState by viewModel.saveState.collectAsState()
    val state = saveState as? SaveState.Loaded ?: return
    val gd = state.saveData.data.gameData

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("玩家进度") },
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
            NumberField(label = "金币", value = gd.playerProgress.coins, onValueChange = { viewModel.updateCoins(it) })
            NumberField(label = "任务解锁等级", value = gd.questSystem.unlockedLevel, onValueChange = { viewModel.updateUnlockedLevel(it) })
            NumberField(label = "全局等级", value = gd.globalLevelSystem.globalLevel, onValueChange = { viewModel.updateGlobalLevel(it) })
            NumberField(label = "全局经验", value = gd.globalLevelSystem.globalExp, onValueChange = { viewModel.updateGlobalExp(it) })
        }
    }
}
