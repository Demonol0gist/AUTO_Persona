package com.example.auto_persona.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.auto_persona.ui.screens.*
import com.example.auto_persona.viewmodel.SaveEditorViewModel

object Routes {
    const val HOME = "home"
    const val PLAYER_INFO = "player_info"
    const val CHARACTER_STATS = "character_stats"
    const val CHARACTER_SYSTEM = "character_system"
    const val PLAYER_PROGRESS = "player_progress"
    const val TIME_SYSTEM = "time_system"
    const val INVENTORY = "inventory"
    const val SHOP = "shop"
    const val DATE_HISTORY = "date_history"
    const val TRAVEL = "travel"
    const val GAME_SETTINGS = "game_settings"
    const val APP_SETTINGS = "app_settings"
    const val DIARY_LIST = "diary_list"
    const val DIARY_DETAIL = "diary_detail/{index}"
    const val PROMPTS_LIST = "prompts_list"
    const val PROMPT_EDIT = "prompt_edit/{promptKey}"
    const val RAW_JSON = "raw_json"
    const val AI_CONFIG = "ai_config"
    const val AI_PERSONA_CREATE = "ai_persona_create"

    fun diaryDetail(index: Int) = "diary_detail/$index"
    fun promptEdit(key: String) = "prompt_edit/$key"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: SaveEditorViewModel = viewModel()
) {
    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.PLAYER_INFO) {
            PlayerInfoScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.CHARACTER_STATS) {
            CharacterStatsScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.CHARACTER_SYSTEM) {
            CharacterSystemScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.PLAYER_PROGRESS) {
            PlayerProgressScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.TIME_SYSTEM) {
            TimeSystemScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.INVENTORY) {
            InventoryScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.SHOP) {
            ShopScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.DATE_HISTORY) {
            DateHistoryScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.TRAVEL) {
            TravelScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.GAME_SETTINGS) {
            GameSettingsScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.APP_SETTINGS) {
            AppSettingsScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.DIARY_LIST) {
            DiaryListScreen(navController = navController, viewModel = viewModel)
        }

        composable(
            route = Routes.DIARY_DETAIL,
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index") ?: 0
            DiaryDetailScreen(navController = navController, viewModel = viewModel, index = index)
        }

        composable(Routes.PROMPTS_LIST) {
            PromptsListScreen(navController = navController, viewModel = viewModel)
        }

        composable(
            route = Routes.PROMPT_EDIT,
            arguments = listOf(navArgument("promptKey") { type = NavType.StringType })
        ) { backStackEntry ->
            val key = backStackEntry.arguments?.getString("promptKey") ?: return@composable
            PromptEditScreen(navController = navController, viewModel = viewModel, promptKey = key)
        }

        composable(Routes.RAW_JSON) {
            RawJsonScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.AI_CONFIG) {
            AiConfigScreen(navController = navController)
        }

        composable(Routes.AI_PERSONA_CREATE) {
            AiPersonaCreateScreen(navController = navController, saveViewModel = viewModel)
        }
    }
}
