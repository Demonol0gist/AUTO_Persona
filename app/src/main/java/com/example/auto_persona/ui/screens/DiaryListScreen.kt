package com.example.auto_persona.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.auto_persona.data.model.DiaryEntry
import com.example.auto_persona.navigation.Routes
import com.example.auto_persona.viewmodel.SaveEditorViewModel
import com.example.auto_persona.viewmodel.SaveState
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryListScreen(
    navController: NavHostController,
    viewModel: SaveEditorViewModel
) {
    val saveState by viewModel.saveState.collectAsState()
    val state = saveState as? SaveState.Loaded ?: return
    val diary = state.saveData.data.diary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("日记 (${diary.size} 条)") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.addDiaryEntry(DiaryEntry(diaryId = UUID.randomUUID().toString(), mode = "Custom")) }) {
                Icon(Icons.Default.Add, "添加条目")
            }
        }
    ) { padding ->
        if (diary.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("暂无日记，点击 + 添加", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(diary, key = { _, e -> e.diaryId.ifEmpty { e.hashCode().toString() } }) { index, entry ->
                    Card(modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Routes.diaryDetail(index)) }) {
                        Column(Modifier.padding(12.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f)) {
                                    Text("${entry.date} ${entry.time}", style = MaterialTheme.typography.labelMedium)
                                    Text(entry.mode, style = MaterialTheme.typography.titleSmall)
                                }
                                IconButton(onClick = { viewModel.removeDiaryEntry(index) }) {
                                    Icon(Icons.Default.Delete, "删除", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            Text(entry.content.take(120), style = MaterialTheme.typography.bodyMedium, maxLines = 3, overflow = TextOverflow.Ellipsis)
                            Text("好感度: ${entry.affection}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
