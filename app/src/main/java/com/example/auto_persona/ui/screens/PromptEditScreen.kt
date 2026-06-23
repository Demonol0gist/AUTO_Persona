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
import com.example.auto_persona.data.model.PromptData
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
    val prompt by remember {
        derivedStateOf {
            val prompts = (saveState as? SaveState.Loaded)?.saveData?.data?.prompts ?: return@derivedStateOf null
            when (promptKey) {
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
            }
        }
    }
    if (prompt == null) return

    var name by remember(prompt) { mutableStateOf(prompt!!.data.name) }
    var description by remember(prompt) { mutableStateOf(prompt!!.data.description) }
    var personality by remember(prompt) { mutableStateOf(prompt!!.data.personality) }
    var scenario by remember(prompt) { mutableStateOf(prompt!!.data.scenario) }
    var creatorNotes by remember(prompt) { mutableStateOf(prompt!!.data.creatorNotes) }
    var firstMes by remember(prompt) { mutableStateOf(prompt!!.data.firstMes ?: "") }
    var tags by remember(prompt) { mutableStateOf(prompt!!.data.tags) }

    fun savePrompt() {
        val newPrompt = prompt!!.copy(data = prompt!!.data.copy(name = name, description = description, personality = personality, scenario = scenario, creatorNotes = creatorNotes, firstMes = firstMes.ifEmpty { null }, tags = tags))
        viewModel.updatePrompt(promptKey, newPrompt)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(displayNameForPromptKey(promptKey)) },
                actions = { TextButton(onClick = { savePrompt(); navController.popBackStack() }) { Text("保存") } },
                navigationIcon = { IconButton(onClick = { savePrompt(); navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField("名称", name, { name = it })
            TextField("描述", description, { description = it }, singleLine = false)
            TextField("性格", personality, { personality = it }, singleLine = false)
            TextField("场景", scenario, { scenario = it }, singleLine = false)
            TextField("创作者笔记", creatorNotes, { creatorNotes = it }, singleLine = false)
            if (prompt!!.data.firstMes != null || firstMes.isNotEmpty()) {
                TextField("开场白", firstMes, { firstMes = it }, singleLine = false)
            }
            ChipGroup("标签", tags, { tags = it })
            Spacer(Modifier.height(8.dp))
            TextField("规格", prompt!!.spec, {}, readOnly = true)
            TextField("规格版本", prompt!!.specVersion, {}, readOnly = true)
        }
    }
}
