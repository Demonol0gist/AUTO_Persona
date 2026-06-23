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
    val coins by remember { derivedStateOf { (saveState as? SaveState.Loaded)?.saveData?.data?.gameData?.playerProgress?.coins ?: 0 } }
    val qLevel by remember { derivedStateOf { (saveState as? SaveState.Loaded)?.saveData?.data?.gameData?.questSystem?.unlockedLevel ?: 0 } }
    val gLevel by remember { derivedStateOf { (saveState as? SaveState.Loaded)?.saveData?.data?.gameData?.globalLevelSystem?.globalLevel ?: 1 } }
    val gExp by remember { derivedStateOf { (saveState as? SaveState.Loaded)?.saveData?.data?.gameData?.globalLevelSystem?.globalExp ?: 0 } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("玩家进度") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NumberField("金币", coins, { viewModel.updateCoins(it) })
            NumberField("任务解锁等级", qLevel, { viewModel.updateUnlockedLevel(it) })
            NumberField("全局等级", gLevel, { viewModel.updateGlobalLevel(it) })
            NumberField("全局经验", gExp, { viewModel.updateGlobalExp(it) })
        }
    }
}
