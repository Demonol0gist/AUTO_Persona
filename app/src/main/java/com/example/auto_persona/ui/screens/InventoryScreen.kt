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
    val inventory by remember { derivedStateOf { (saveState as? SaveState.Loaded)?.saveData?.data?.gameData?.inventory } }
    if (inventory == null) return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("背包") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.updateItems(inventory!!.items + InventoryItem()) }) {
                Icon(Icons.Default.Add, "添加物品")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(1) { SectionHeader("物品 (${inventory!!.items.size})") }
            if (inventory!!.items.isEmpty()) {
                items(1) { Card(Modifier.fillMaxWidth()) { Text("暂无物品，点击 + 添加", Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium) } }
            }
            itemsIndexed(inventory!!.items, key = { _, item -> item.id.hashCode() + item.name.hashCode() }) { index, item ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("物品 ${index + 1}", style = MaterialTheme.typography.titleSmall)
                            IconButton(onClick = { viewModel.updateItems(inventory!!.items.toMutableList().apply { removeAt(index) }) }) {
                                Icon(Icons.Default.Delete, "删除", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                        TextField("ID", item.id, { val l = inventory!!.items.toMutableList(); l[index] = item.copy(id = it); viewModel.updateItems(l) })
                        TextField("名称", item.name, { val l = inventory!!.items.toMutableList(); l[index] = item.copy(name = it); viewModel.updateItems(l) })
                        NumberField("数量", item.quantity, { val l = inventory!!.items.toMutableList(); l[index] = item.copy(quantity = it); viewModel.updateItems(l) })
                        TextField("类型", item.type, { val l = inventory!!.items.toMutableList(); l[index] = item.copy(type = it); viewModel.updateItems(l) })
                    }
                }
            }
            items(1) { SectionHeader("装备") }
            items(1) {
                val eq = inventory!!.equipment
                TextField("武器", eq.weapon ?: "", { viewModel.updateWeapon(it.ifEmpty { null }) })
                TextField("防具", eq.armor ?: "", { viewModel.updateArmor(it.ifEmpty { null }) })
                TextField("饰品", eq.accessory ?: "", { viewModel.updateAccessory(it.ifEmpty { null }) })
            }
            items(1) { Spacer(Modifier.height(80.dp)) }
        }
    }
}
