package com.example.auto_persona.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.auto_persona.navigation.Routes
import com.example.auto_persona.ui.components.ClickableCard
import com.example.auto_persona.util.allPromptKeys
import com.example.auto_persona.util.displayNameForPromptKey
import com.example.auto_persona.viewmodel.SaveEditorViewModel
import com.example.auto_persona.viewmodel.SaveState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptsListScreen(
    navController: NavHostController,
    viewModel: SaveEditorViewModel
) {
    val saveState by viewModel.saveState.collectAsState()
    val state = saveState as? SaveState.Loaded ?: return
    val prompts = state.saveData.data.prompts

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("人格提示词") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allPromptKeys) { key ->
                val prompt = when (key) {
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
                val personality = prompt?.data?.personality?.take(60) ?: "(空)"
                ClickableCard(
                    title = displayNameForPromptKey(key),
                    subtitle = "性格: $personality",
                    onClick = { navController.navigate(Routes.promptEdit(key)) }
                )
            }
        }
    }
}
