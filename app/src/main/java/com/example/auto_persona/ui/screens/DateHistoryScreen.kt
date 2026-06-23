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
import com.example.auto_persona.ui.components.NumberField
import com.example.auto_persona.viewmodel.SaveEditorViewModel
import com.example.auto_persona.viewmodel.SaveState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateHistoryScreen(
    navController: NavHostController,
    viewModel: SaveEditorViewModel
) {
    val saveState by viewModel.saveState.collectAsState()
    val dh by remember { derivedStateOf { (saveState as? SaveState.Loaded)?.saveData?.data?.gameData?.dateHistory } }
    if (dh == null) return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("约会记录") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NumberField("自定义约会", dh!!.custom, { viewModel.updateDateCustom(it) })
            NumberField("咖啡厅约会", dh!!.cafe, { viewModel.updateDateCafe(it) })
            NumberField("公园约会", dh!!.park, { viewModel.updateDatePark(it) })
            NumberField("餐厅约会", dh!!.restaurant, { viewModel.updateDateRestaurant(it) })
            NumberField("海滩约会", dh!!.beach, { viewModel.updateDateBeach(it) })
            NumberField("夜市约会", dh!!.nightmarket, { viewModel.updateDateNightmarket(it) })
            NumberField("夜路约会", dh!!.nightpath, { viewModel.updateDateNightpath(it) })
        }
    }
}
