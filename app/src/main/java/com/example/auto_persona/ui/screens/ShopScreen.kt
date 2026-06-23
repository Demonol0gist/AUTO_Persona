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
import com.example.auto_persona.ui.components.NumberField
import com.example.auto_persona.ui.components.SectionHeader
import com.example.auto_persona.ui.components.TextField
import com.example.auto_persona.viewmodel.SaveEditorViewModel
import com.example.auto_persona.viewmodel.SaveState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShopScreen(
    navController: NavHostController,
    viewModel: SaveEditorViewModel
) {
    val saveState by viewModel.saveState.collectAsState()
    val shop by remember { derivedStateOf { (saveState as? SaveState.Loaded)?.saveData?.data?.gameData?.shopSystem } }
    if (shop == null) return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("商店") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } }
            )
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(1) { SectionHeader("服装购买状态") }
            items(1) {
                val purchases = shop!!.purchases
                FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (i in 1..20) {
                        val checked = when (i) {
                            1 -> purchases.outfit1; 2 -> purchases.outfit2; 3 -> purchases.outfit3
                            4 -> purchases.outfit4; 5 -> purchases.outfit5; 6 -> purchases.outfit6
                            7 -> purchases.outfit7; 8 -> purchases.outfit8; 9 -> purchases.outfit9
                            10 -> purchases.outfit10; 11 -> purchases.outfit11; 12 -> purchases.outfit12
                            13 -> purchases.outfit13; 14 -> purchases.outfit14; 15 -> purchases.outfit15
                            16 -> purchases.outfit16; 17 -> purchases.outfit17; 18 -> purchases.outfit18
                            19 -> purchases.outfit19; 20 -> purchases.outfit20
                            else -> false
                        }
                        FilterChip(
                            selected = checked,
                            onClick = {
                                val newPurchases = when (i) {
                                    1 -> purchases.copy(outfit1 = !checked); 2 -> purchases.copy(outfit2 = !checked)
                                    3 -> purchases.copy(outfit3 = !checked); 4 -> purchases.copy(outfit4 = !checked)
                                    5 -> purchases.copy(outfit5 = !checked); 6 -> purchases.copy(outfit6 = !checked)
                                    7 -> purchases.copy(outfit7 = !checked); 8 -> purchases.copy(outfit8 = !checked)
                                    9 -> purchases.copy(outfit9 = !checked); 10 -> purchases.copy(outfit10 = !checked)
                                    11 -> purchases.copy(outfit11 = !checked); 12 -> purchases.copy(outfit12 = !checked)
                                    13 -> purchases.copy(outfit13 = !checked); 14 -> purchases.copy(outfit14 = !checked)
                                    15 -> purchases.copy(outfit15 = !checked); 16 -> purchases.copy(outfit16 = !checked)
                                    17 -> purchases.copy(outfit17 = !checked); 18 -> purchases.copy(outfit18 = !checked)
                                    19 -> purchases.copy(outfit19 = !checked); 20 -> purchases.copy(outfit20 = !checked)
                                    else -> purchases
                                }
                                viewModel.updateOutfitPurchases(newPurchases)
                            },
                            label = { Text("服装${i}") }
                        )
                    }
                }
            }
            items(1) { SectionHeader("礼物库存") }
            items(1) {
                val gifts = shop!!.giftInventory
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    NumberField("礼物 1", gifts.gift1, { viewModel.updateGiftInventory(gifts.copy(gift1 = it)) })
                    NumberField("礼物 2", gifts.gift2, { viewModel.updateGiftInventory(gifts.copy(gift2 = it)) })
                    NumberField("礼物 3", gifts.gift3, { viewModel.updateGiftInventory(gifts.copy(gift3 = it)) })
                    NumberField("礼物 4", gifts.gift4, { viewModel.updateGiftInventory(gifts.copy(gift4 = it)) })
                    NumberField("礼物 5", gifts.gift5, { viewModel.updateGiftInventory(gifts.copy(gift5 = it)) })
                    NumberField("礼物 6", gifts.gift6, { viewModel.updateGiftInventory(gifts.copy(gift6 = it)) })
                    NumberField("礼物 7", gifts.gift7, { viewModel.updateGiftInventory(gifts.copy(gift7 = it)) })
                    NumberField("礼物 8", gifts.gift8, { viewModel.updateGiftInventory(gifts.copy(gift8 = it)) })
                    NumberField("礼物 9", gifts.gift9, { viewModel.updateGiftInventory(gifts.copy(gift9 = it)) })
                    NumberField("礼物 10", gifts.gift10, { viewModel.updateGiftInventory(gifts.copy(gift10 = it)) })
                    NumberField("礼物 11", gifts.gift11, { viewModel.updateGiftInventory(gifts.copy(gift11 = it)) })
                    NumberField("礼物 12", gifts.gift12, { viewModel.updateGiftInventory(gifts.copy(gift12 = it)) })
                }
            }
            items(1) { SectionHeader("旅行库存") }
            val travelEntries = shop!!.travelInventory.entries.toList()
            if (travelEntries.isEmpty()) {
                items(1) { Text("无旅行库存物品", style = MaterialTheme.typography.bodyMedium) }
            } else {
                items(travelEntries, key = { it.key }) { (key, value) ->
                    var currentValue by remember { mutableIntStateOf(value) }
                    NumberField(
                        label = key.replace("_", " "),
                        value = currentValue,
                        onValueChange = { newVal ->
                            currentValue = newVal
                            viewModel.updateTravelInventory(shop!!.travelInventory.toMutableMap().apply { put(key, newVal) })
                        }
                    )
                }
            }
            items(1) { SectionHeader("当前标签页") }
            items(1) { TextField("商店标签页", shop!!.currentTab, { viewModel.updateShopTab(it) }) }
            items(1) { Spacer(Modifier.height(16.dp)) }
        }
    }
}
