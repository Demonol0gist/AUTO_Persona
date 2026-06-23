package com.example.auto_persona.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.auto_persona.navigation.Routes
import com.example.auto_persona.ui.components.ClickableCard
import com.example.auto_persona.viewmodel.SaveEditorViewModel
import com.example.auto_persona.viewmodel.SaveState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: SaveEditorViewModel
) {
    val saveState by viewModel.saveState.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showMenu by remember { mutableStateOf(false) }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? -> uri?.let { viewModel.importSave(it) } }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? -> uri?.let { viewModel.exportSave(it) } }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearSnackbar() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("妹居物语 存档编辑器") },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) { Icon(Icons.Default.MoreVert, "菜单") }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem({ Text("导入存档") }, { showMenu = false; importLauncher.launch(arrayOf("application/json")) })
                            DropdownMenuItem({ Text("导出存档") }, { showMenu = false; exportLauncher.launch("save_modified.json") })
                            DropdownMenuItem({ Text("查看原始 JSON") }, { showMenu = false; navController.navigate(Routes.RAW_JSON) })
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when (val state = saveState) {
            is SaveState.Loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) { CircularProgressIndicator() }
            is SaveState.Error -> Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.Center) {
                Text("加载存档数据失败", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text(state.message, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { viewModel.loadTemplate() }) { Text("重试") }
            }
            is SaveState.Loaded -> {
                val summary by remember { derivedStateOf { buildSummary(state) } }
                LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(1) {
                        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                            Column(Modifier.padding(16.dp)) { summary.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) } }
                        }
                    }
                    items(1) { Text("玩家", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp)) }
                    items(1) { ClickableCard("玩家信息", "${state.saveData.data.gameData.playerInfo.name.ifEmpty { "(空)" }} | ${state.saveData.data.gameData.gender}") { navController.navigate(Routes.PLAYER_INFO) } }
                    items(1) { Text("角色", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp)) }
                    items(1) { ClickableCard("角色属性", "好感度: ${state.saveData.data.gameData.characterStats.affection} / 信任度: ${state.saveData.data.gameData.characterStats.trust}") { navController.navigate(Routes.CHARACTER_STATS) } }
                    items(1) { ClickableCard("角色系统", "${state.saveData.data.gameData.characterSystemData.character.name} - ${state.saveData.data.gameData.characterSystemData.character.personality}") { navController.navigate(Routes.CHARACTER_SYSTEM) } }
                    items(1) { Text("进度", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp)) }
                    items(1) { ClickableCard("玩家进度", "金币: ${state.saveData.data.gameData.playerProgress.coins} | 等级: ${state.saveData.data.gameData.globalLevelSystem.globalLevel}") { navController.navigate(Routes.PLAYER_PROGRESS) } }
                    items(1) { Text("时间与设置", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp)) }
                    items(1) { ClickableCard("时间系统", "第${state.saveData.data.gameData.timeSystem.currentDay}天 | 行动: ${state.saveData.data.gameData.timeSystem.actionsUsed}/${state.saveData.data.gameData.timeSystem.dailyActions}") { navController.navigate(Routes.TIME_SYSTEM) } }
                    items(1) { ClickableCard("游戏设置", "音乐: ${state.saveData.data.gameData.settings.musicVolume} | 音效: ${state.saveData.data.gameData.settings.soundVolume}") { navController.navigate(Routes.GAME_SETTINGS) } }
                    items(1) { ClickableCard("应用设置", "BGM: ${state.saveData.data.settings.bgmVolume} | 语音: ${state.saveData.data.settings.voiceVolume}") { navController.navigate(Routes.APP_SETTINGS) } }
                    items(1) { Text("背包与商店", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp)) }
                    items(1) { ClickableCard("背包", "${state.saveData.data.gameData.inventory.items.size} 件物品") { navController.navigate(Routes.INVENTORY) } }
                    items(1) { ClickableCard("商店", "服装与礼物") { navController.navigate(Routes.SHOP) } }
                    items(1) { Text("数据", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp)) }
                    items(1) { ClickableCard("约会记录", "7种约会类型") { navController.navigate(Routes.DATE_HISTORY) } }
                    items(1) { ClickableCard("旅行系统", if (state.saveData.data.gameData.travelSystem.isTravelMode) "旅行中" else "未旅行") { navController.navigate(Routes.TRAVEL) } }
                    items(1) { Text("内容", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp)) }
                    items(1) { ClickableCard("日记", "${state.saveData.data.diary.size} 条记录") { navController.navigate(Routes.DIARY_LIST) } }
                    items(1) { ClickableCard("人格提示词", "9种人格变体") { navController.navigate(Routes.PROMPTS_LIST) } }
                }
            }
        }
    }
}

private fun buildSummary(state: SaveState.Loaded): List<String> {
    val gd = state.saveData.data.gameData
    return listOf(
        "来源: ${state.source}",
        "槽位: ${state.saveData.slotId} | 天数: ${gd.timeSystem.currentDay}",
        "${gd.characterSystemData.character.name} - ${gd.characterSystemData.character.personality}",
        "好感度: ${gd.characterStats.affection} | 信任度: ${gd.characterStats.trust}",
        "金币: ${gd.playerProgress.coins} | 等级: ${gd.globalLevelSystem.globalLevel}"
    )
}
