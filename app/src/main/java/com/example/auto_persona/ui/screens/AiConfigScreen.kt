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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.auto_persona.ui.components.TextField
import com.example.auto_persona.viewmodel.AiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiConfigScreen(
    navController: NavHostController,
    aiViewModel: AiViewModel = viewModel()
) {
    val config by aiViewModel.config.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI 模型配置") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("AI 接入设置", style = MaterialTheme.typography.titleMedium)
            Text("默认接入 DeepSeek，可切换为其他兼容 OpenAI 接口的模型", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            TextField(label = "API Key", value = config.apiKey, onValueChange = { aiViewModel.updateApiKey(it) })
            TextField(label = "Base URL", value = config.baseUrl, onValueChange = { aiViewModel.updateBaseUrl(it) })
            TextField(label = "模型名称", value = config.model, onValueChange = { aiViewModel.updateModel(it) })

            Spacer(Modifier.height(8.dp))
            Text("常用模型参考", style = MaterialTheme.typography.labelLarge)
            Text("DeepSeek: deepseek-v4-flash / deepseek-v4-pro", style = MaterialTheme.typography.bodySmall)
            Text("OpenAI: gpt-4o / gpt-4o-mini", style = MaterialTheme.typography.bodySmall)
            Text("其他兼容接口: 填入对应 Base URL 和模型名即可", style = MaterialTheme.typography.bodySmall)
        }
    }
}
