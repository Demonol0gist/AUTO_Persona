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
import com.example.auto_persona.ui.components.TextField
import com.example.auto_persona.viewmodel.SaveEditorViewModel
import com.example.auto_persona.viewmodel.SaveState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailScreen(
    navController: NavHostController,
    viewModel: SaveEditorViewModel,
    index: Int
) {
    val saveState by viewModel.saveState.collectAsState()
    val state = saveState as? SaveState.Loaded ?: return
    val diary = state.saveData.data.diary

    if (index !in diary.indices) return
    val entry = diary[index]

    var date by remember(entry) { mutableStateOf(entry.date) }
    var time by remember(entry) { mutableStateOf(entry.time) }
    var affection by remember(entry) { mutableIntStateOf(entry.affection) }
    var content by remember(entry) { mutableStateOf(entry.content) }
    var mode by remember(entry) { mutableStateOf(entry.mode) }

    fun save() {
        viewModel.updateDiaryEntry(index, entry.copy(date = date, time = time, affection = affection, content = content, mode = mode))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("日记详情 ${index + 1}") },
                navigationIcon = {
                    IconButton(onClick = { save(); navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                },
                actions = { TextButton(onClick = { save() }) { Text("保存") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(label = "日期", value = date, onValueChange = { date = it })
            TextField(label = "时间", value = time, onValueChange = { time = it })
            NumberField(label = "好感度", value = affection, onValueChange = { affection = it })
            TextField(label = "模式", value = mode, onValueChange = { mode = it })
            TextField(label = "内容", value = content, onValueChange = { content = it }, singleLine = false)
            HorizontalDivider()
            TextField(label = "日记 ID", value = entry.diaryId, onValueChange = {}, readOnly = true)
        }
    }
}
