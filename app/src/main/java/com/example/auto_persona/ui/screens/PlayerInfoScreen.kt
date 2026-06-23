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
import com.example.auto_persona.ui.components.SectionHeader
import com.example.auto_persona.ui.components.SwitchRow
import com.example.auto_persona.ui.components.TextField
import com.example.auto_persona.viewmodel.SaveEditorViewModel
import com.example.auto_persona.viewmodel.SaveState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerInfoScreen(
    navController: NavHostController,
    viewModel: SaveEditorViewModel
) {
    val saveState by viewModel.saveState.collectAsState()
    val state = saveState as? SaveState.Loaded ?: return
    val pi = state.saveData.data.gameData.playerInfo

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("玩家信息") },
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
            TextField(
                label = "姓名",
                value = pi.name,
                onValueChange = { viewModel.updatePlayerName(it) }
            )
            TextField(
                label = "生日",
                value = pi.birthday,
                onValueChange = { viewModel.updatePlayerBirthday(it) }
            )
            TextField(
                label = "身份",
                value = pi.identity,
                onValueChange = { viewModel.updatePlayerIdentity(it) }
            )
            SwitchRow(
                label = "已领取",
                checked = pi.hasCollected,
                onCheckedChange = { viewModel.updatePlayerHasCollected(it) }
            )
            SectionHeader("性别")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = state.saveData.data.gameData.gender == "brother",
                    onClick = { viewModel.updateGender("brother") },
                    label = { Text("哥哥 (brother)") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = state.saveData.data.gameData.gender == "sister",
                    onClick = { viewModel.updateGender("sister") },
                    label = { Text("妹妹 (sister)") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
