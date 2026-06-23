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
fun TravelScreen(
    navController: NavHostController,
    viewModel: SaveEditorViewModel
) {
    val saveState by viewModel.saveState.collectAsState()
    val state = saveState as? SaveState.Loaded ?: return
    val ts = state.saveData.data.gameData.travelSystem

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("旅行系统") },
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
            SwitchRow("旅行模式", ts.isTravelMode, { viewModel.updateTravelMode(it) })
            SwitchRow("已显示夜间对话", ts.hasShownNightDialogue, { viewModel.updateHasShownNightDialogue(it) })

            TextField(label = "当前目的地", value = ts.currentDestination ?: "", onValueChange = { viewModel.updateCurrentDestination(it.ifEmpty { null }) })
            TextField(label = "目的地城市名", value = ts.destinationCityName ?: "", onValueChange = { viewModel.updateDestinationCityName(it.ifEmpty { null }) })
            TextField(label = "当前城市", value = ts.currentCity ?: "", onValueChange = { viewModel.updateCurrentCity(it.ifEmpty { null }) })
            TextField(label = "旅行状态", value = ts.travelState ?: "", onValueChange = { viewModel.updateTravelState(it.ifEmpty { null }) })
            TextField(label = "旅行开始时间", value = ts.travelStartTime ?: "", onValueChange = { viewModel.updateTravelStartTime(it.ifEmpty { null }) })
            TextField(label = "旅行结束时间", value = ts.travelEndTime ?: "", onValueChange = { viewModel.updateTravelEndTime(it.ifEmpty { null }) })

            ChipGroup(label = "已解锁目的地", items = ts.unlockedDestinations, onItemsChange = { viewModel.updateUnlockedDestinations(it) })
            ChipGroup(label = "当前景点", items = ts.currentSpots, onItemsChange = { viewModel.updateCurrentSpots(it) })
        }
    }
}
