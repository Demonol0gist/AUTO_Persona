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
    val playerInfo by remember { derivedStateOf { (saveState as? SaveState.Loaded)?.saveData?.data?.gameData?.playerInfo } }
    val gender by remember { derivedStateOf { (saveState as? SaveState.Loaded)?.saveData?.data?.gameData?.gender ?: "" } }
    if (playerInfo == null) return

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
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField("姓名", playerInfo!!.name, { viewModel.updatePlayerName(it) })
            TextField("生日", playerInfo!!.birthday, { viewModel.updatePlayerBirthday(it) })
            TextField("身份", playerInfo!!.identity, { viewModel.updatePlayerIdentity(it) })
            SwitchRow("已领取", playerInfo!!.hasCollected, { viewModel.updatePlayerHasCollected(it) })
            SectionHeader("性别")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(gender == "brother", { viewModel.updateGender("brother") }, { Text("哥哥 (brother)") }, Modifier.weight(1f))
                FilterChip(gender == "sister", { viewModel.updateGender("sister") }, { Text("妹妹 (sister)") }, Modifier.weight(1f))
            }
        }
    }
}
