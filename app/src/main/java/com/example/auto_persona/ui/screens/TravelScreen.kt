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
    val ts by remember { derivedStateOf { (saveState as? SaveState.Loaded)?.saveData?.data?.gameData?.travelSystem } }
    if (ts == null) return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("旅行系统") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SwitchRow("旅行模式", ts!!.isTravelMode, { viewModel.updateTravelMode(it) })
            SwitchRow("已显示夜间对话", ts!!.hasShownNightDialogue, { viewModel.updateHasShownNightDialogue(it) })
            TextField("当前目的地", ts!!.currentDestination ?: "", { viewModel.updateCurrentDestination(it.ifEmpty { null }) })
            TextField("目的地城市名", ts!!.destinationCityName ?: "", { viewModel.updateDestinationCityName(it.ifEmpty { null }) })
            TextField("当前城市", ts!!.currentCity ?: "", { viewModel.updateCurrentCity(it.ifEmpty { null }) })
            TextField("旅行状态", ts!!.travelState ?: "", { viewModel.updateTravelState(it.ifEmpty { null }) })
            TextField("旅行开始时间", ts!!.travelStartTime ?: "", { viewModel.updateTravelStartTime(it.ifEmpty { null }) })
            TextField("旅行结束时间", ts!!.travelEndTime ?: "", { viewModel.updateTravelEndTime(it.ifEmpty { null }) })
            ChipGroup("已解锁目的地", ts!!.unlockedDestinations, { viewModel.updateUnlockedDestinations(it) })
            ChipGroup("当前景点", ts!!.currentSpots, { viewModel.updateCurrentSpots(it) })
        }
    }
}
