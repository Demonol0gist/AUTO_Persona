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
import com.example.auto_persona.data.model.DiaryEntry
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
    val entry by remember { derivedStateOf { (saveState as? SaveState.Loaded)?.saveData?.data?.diary?.getOrNull(index) } }
    if (entry == null) return

    var date by remember(entry) { mutableStateOf(entry!!.date) }
    var time by remember(entry) { mutableStateOf(entry!!.time) }
    var affection by remember(entry) { mutableIntStateOf(entry!!.affection) }
    var content by remember(entry) { mutableStateOf(entry!!.content) }
    var mode by remember(entry) { mutableStateOf(entry!!.mode) }

    fun save() { viewModel.updateDiaryEntry(index, entry!!.copy(date = date, time = time, affection = affection, content = content, mode = mode)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("日记详情 ${index + 1}") },
                navigationIcon = { IconButton(onClick = { save(); navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } },
                actions = { TextButton(onClick = { save() }) { Text("保存") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField("日期", date, { date = it })
            TextField("时间", time, { time = it })
            NumberField("好感度", affection, { affection = it })
            TextField("模式", mode, { mode = it })
            TextField("内容", content, { content = it }, singleLine = false)
            HorizontalDivider()
            TextField("日记 ID", entry!!.diaryId, {}, readOnly = true)
        }
    }
}
