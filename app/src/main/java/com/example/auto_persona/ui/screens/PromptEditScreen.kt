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
import com.example.auto_persona.ui.components.TextField
import com.example.auto_persona.util.displayNameForPromptKey
import com.example.auto_persona.viewmodel.SaveEditorViewModel
import com.example.auto_persona.viewmodel.SaveState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptEditScreen(
    navController: NavHostController,
    viewModel: SaveEditorViewModel,
    promptKey: String
) {
    val saveState by viewModel.saveState.collectAsState()
    val state = saveState as? SaveState.Loaded ?: return
    val prompts = state.saveData.data.prompts

    val prompt = when (promptKey) {
        "sister-null" -> prompts.sisterNull
        "sister-verylow" -> prompts.sisterVerylow
        "sister-low" -> prompts.sisterLow
        "sister-medium" -> prompts.sisterMedium
        "sister-high" -> prompts.sisterHigh
        "sister-dilei" -> prompts.sisterDilei
        "sister-kindergarten" -> prompts.sisterKindergarten
        "sister-tutor" -> prompts.sisterTutor
        "sister-kemonomimi" -> prompts.sisterKemonomimi
        "sister-kemonomimi-cat" -> prompts.sisterKemonomimiCat
        else -> null
    } ?: return

    var name by remember(prompt) { mutableStateOf(prompt.data.name) }
    var description by remember(prompt) { mutableStateOf(prompt.data.description) }
    var personality by remember(prompt) { mutableStateOf(prompt.data.personality) }
    var scenario by remember(prompt) { mutableStateOf(prompt.data.scenario) }
    var creatorNotes by remember(prompt) { mutableStateOf(prompt.data.creatorNotes) }
    var firstMes by remember(prompt) { mutableStateOf(prompt.data.firstMes ?: "") }
    var tags by remember(prompt) { mutableStateOf(prompt.data.tags) }

    fun savePrompt() {
        val newPrompt = prompt.copy(data = prompt.data.copy(
            name = name, description = description, personality = personality,
            scenario = scenario, creatorNotes = creatorNotes,
            firstMes = firstMes.ifEmpty { null }, tags = tags
        ))
        viewModel.updatePrompt(promptKey, newPrompt)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(displayNameForPromptKey(promptKey)) },
                actions = { TextButton(onClick = { savePrompt(); navController.popBackStack() }) { Text("保存") } },
                navigationIcon = {
                    IconButton(onClick = { savePrompt(); navController.popBackStack() }) {
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
            TextField(label = "名称", value = name, onValueChange = { name = it })
            TextField(label = "描述", value = description, onValueChange = { description = it }, singleLine = false)
            TextField(label = "性格", value = personality, onValueChange = { personality = it }, singleLine = false)
            TextField(label = "场景", value = scenario, onValueChange = { scenario = it }, singleLine = false)
            TextField(label = "创作者笔记", value = creatorNotes, onValueChange = { creatorNotes = it }, singleLine = false)
            if (prompt.data.firstMes != null || firstMes.isNotEmpty()) {
                TextField(label = "开场白", value = firstMes, onValueChange = { firstMes = it }, singleLine = false)
            }
            ChipGroup(label = "标签", items = tags, onItemsChange = { tags = it })
            Spacer(modifier = Modifier.height(8.dp))
            TextField(label = "规格", value = prompt.spec, onValueChange = {}, readOnly = true)
            TextField(label = "规格版本", value = prompt.specVersion, onValueChange = {}, readOnly = true)
        }
    }
}
