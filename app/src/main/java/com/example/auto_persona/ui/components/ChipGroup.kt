package com.example.auto_persona.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipGroup(
    label: String,
    items: List<String>,
    onItemsChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddField by remember { mutableStateOf(false) }
    var newItemText by remember { mutableStateOf("") }

    Text(
        text = label,
        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach { item ->
            InputChip(
                selected = false,
                onClick = {
                    onItemsChange(items.filter { it != item })
                },
                label = { Text(item) },
                trailingIcon = {
                    IconButton(onClick = {
                        onItemsChange(items.filter { it != item })
                    }) {
                        Text("✕", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                    }
                }
            )
        }

        if (showAddField) {
            OutlinedTextField(
                value = newItemText,
                onValueChange = { newItemText = it },
                singleLine = true,
                modifier = Modifier.padding(vertical = 4.dp),
                label = { Text("New item") },
                trailingIcon = {
                    IconButton(onClick = {
                        if (newItemText.isNotBlank()) {
                            onItemsChange(items + newItemText.trim())
                            newItemText = ""
                            showAddField = false
                        }
                    }) {
                        Text("✓")
                    }
                }
            )
        } else {
            AssistChip(
                onClick = { showAddField = true },
                label = { Text("+ Add") }
            )
        }
    }
}
