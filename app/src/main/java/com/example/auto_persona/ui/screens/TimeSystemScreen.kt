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
import com.example.auto_persona.ui.components.ChipGroup
import com.example.auto_persona.ui.components.NumberField
import com.example.auto_persona.ui.components.TextField
import com.example.auto_persona.viewmodel.SaveEditorViewModel
import com.example.auto_persona.viewmodel.SaveState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSystemScreen(
    navController: NavHostController,
    viewModel: SaveEditorViewModel
) {
    val saveState by viewModel.saveState.collectAsState()
    val state = saveState as? SaveState.Loaded ?: return
    val ts = state.saveData.data.gameData.timeSystem

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("时间系统") },
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
            NumberField("当前天数", ts.currentDay, { viewModel.updateCurrentDay(it) })
            NumberField("每日行动次数", ts.dailyActions, { viewModel.updateDailyActions(it) })
            NumberField("已用行动次数", ts.actionsUsed, { viewModel.updateActionsUsed(it) })
            TextField(
                label = "游戏开始日期",
                value = ts.gameStartDate,
                onValueChange = { viewModel.updateGameStartDate(it) }
            )
            ChipGroup(
                label = "特殊事件",
                items = ts.specialEvents,
                onItemsChange = { viewModel.updateTimeSpecialEvents(it) }
            )
        }
    }
}
