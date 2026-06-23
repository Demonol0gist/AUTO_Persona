package com.example.auto_persona.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.auto_persona.data.model.InventoryItem
import com.example.auto_persona.ui.components.NumberField
import com.example.auto_persona.ui.components.SectionHeader
import com.example.auto_persona.ui.components.TextField
import com.example.auto_persona.viewmodel.SaveEditorViewModel
import com.example.auto_persona.viewmodel.SaveState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    navController: NavHostController,
    viewModel: SaveEditorViewModel
) {
    val saveState by viewModel.saveState.collectAsState()
    val state = saveState as? SaveState.Loaded ?: return
    val inventory = state.saveData.data.gameData.inventory

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("背包") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.updateItems(inventory.items + InventoryItem())
            }) {
                Icon(Icons.Default.Add, "添加物品")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(1) { SectionHeader("物品 (${inventory.items.size})") }

            if (inventory.items.isEmpty()) {
                items(1) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text("暂无物品，点击 + 添加", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            itemsIndexed(inventory.items) { index, item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("物品 ${index + 1}", style = MaterialTheme.typography.titleSmall)
                            IconButton(onClick = {
                                viewModel.updateItems(inventory.items.toMutableList().apply { removeAt(index) })
                            }) {
                                Icon(Icons.Default.Delete, "删除", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                        TextField(label = "ID", value = item.id, onValueChange = { newId ->
                            val newList = inventory.items.toMutableList()
                            newList[index] = item.copy(id = newId)
                            viewModel.updateItems(newList)
                        })
                        TextField(label = "名称", value = item.name, onValueChange = { newName ->
                            val newList = inventory.items.toMutableList()
                            newList[index] = item.copy(name = newName)
                            viewModel.updateItems(newList)
                        })
                        NumberField(label = "数量", value = item.quantity, onValueChange = { qty ->
                            val newList = inventory.items.toMutableList()
                            newList[index] = item.copy(quantity = qty)
                            viewModel.updateItems(newList)
                        })
                        TextField(label = "类型", value = item.type, onValueChange = { newType ->
                            val newList = inventory.items.toMutableList()
                            newList[index] = item.copy(type = newType)
                            viewModel.updateItems(newList)
                        })
                    }
                }
            }

            items(1) { SectionHeader("装备") }
            items(1) {
                val eq = inventory.equipment
                TextField(label = "武器", value = eq.weapon ?: "", onValueChange = { viewModel.updateWeapon(it.ifEmpty { null }) })
                TextField(label = "防具", value = eq.armor ?: "", onValueChange = { viewModel.updateArmor(it.ifEmpty { null }) })
                TextField(label = "饰品", value = eq.accessory ?: "", onValueChange = { viewModel.updateAccessory(it.ifEmpty { null }) })
            }

            items(1) { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
