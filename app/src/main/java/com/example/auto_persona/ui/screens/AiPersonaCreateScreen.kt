package com.example.auto_persona.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.auto_persona.ui.components.SectionHeader
import com.example.auto_persona.util.allPromptKeys
import com.example.auto_persona.util.displayNameForPromptKey
import com.example.auto_persona.viewmodel.AiViewModel
import com.example.auto_persona.viewmodel.SaveEditorViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AiPersonaCreateScreen(
    navController: NavHostController,
    saveViewModel: SaveEditorViewModel,
    aiViewModel: AiViewModel = viewModel()
) {
    val isGenerating by aiViewModel.isGenerating.collectAsState()
    val generatedPersona by aiViewModel.generatedPersona.collectAsState()
    val error by aiViewModel.error.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var userInput by remember { mutableStateOf("") }
    var extraInput by remember { mutableStateOf("") }
    var targetKey by remember { mutableStateOf("sister-null") }
    var lastUsedInput by remember { mutableStateOf("") }
    var showRegenerate by remember { mutableStateOf(false) }

    LaunchedEffect(error) {
        error?.let { snackbarHostState.showSnackbar(it); aiViewModel.clearResult() }
    }

    fun doGenerate(extra: String = "") {
        val fullInput = if (extra.isNotBlank()) "$lastUsedInput\n追加要求: $extra" else userInput
        lastUsedInput = if (extra.isNotBlank()) lastUsedInput else userInput
        extraInput = ""
        aiViewModel.generatePersona(fullInput)
        showRegenerate = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI 生成人设") },
                navigationIcon = {
                    IconButton(onClick = { aiViewModel.clearResult(); navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!showRegenerate) {
                Text("描述你想要的角色", style = MaterialTheme.typography.titleMedium)
                Text("用自然语言描述角色的性格、外貌、背景故事等，AI 将自动生成符合模板格式的人设配置。", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    label = { Text("角色描述") },
                    placeholder = { Text("例如：一个傲娇的猫娘女仆，表面上对主人不屑一顾，实际上非常在意主人的一举一动...") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                    maxLines = 8
                )

                Text("目标槽位", style = MaterialTheme.typography.labelLarge)
                FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    allPromptKeys.forEach { key ->
                        FilterChip(selected = targetKey == key, onClick = { targetKey = key }, label = { Text(displayNameForPromptKey(key).take(10)) })
                    }
                }

                Button(
                    onClick = { doGenerate() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isGenerating && userInput.isNotBlank()
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("生成中...")
                    } else Text("生成人设")
                }
            }

            if (showRegenerate) {
                Text("追加要求（可选）", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = extraInput,
                    onValueChange = { extraInput = it },
                    label = { Text("补充描述") },
                    placeholder = { Text("例如：语气再傲娇一点，多加一些颜文字...") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
                    maxLines = 4
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { doGenerate(extraInput) },
                        modifier = Modifier.weight(1f),
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(4.dp))
                            Text("重新生成中...")
                        } else Text("追加要求重新生成")
                    }
                    OutlinedButton(
                        onClick = { showRegenerate = false; aiViewModel.clearResult(); lastUsedInput = "" },
                        modifier = Modifier.weight(1f)
                    ) { Text("重新开始") }
                }
            }

            if (generatedPersona != null) {
                val p = generatedPersona!!
                SectionHeader("生成结果（可滚动查看）")
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ResultRow("名称", p.data.name)
                        ResultRow("性格", p.data.personality)
                        ResultRow("描述", p.data.description)
                        ResultRow("场景", p.data.scenario)
                        ResultRow("创作者笔记", p.data.creatorNotes)
                        if (p.data.firstMes != null) ResultRow("开场白", p.data.firstMes!!)
                        Text("标签: ${p.data.tags.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { saveViewModel.updatePrompt(targetKey, generatedPersona); aiViewModel.clearResult(); navController.popBackStack() },
                        modifier = Modifier.weight(1f)
                    ) { Text("应用到「${displayNameForPromptKey(targetKey).take(8)}」") }
                }
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp))
    }
}
