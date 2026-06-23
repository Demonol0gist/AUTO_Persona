package com.example.auto_persona

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.auto_persona.navigation.AppNavGraph
import com.example.auto_persona.ui.theme.Auto_personaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Auto_personaTheme {
                val navController = rememberNavController()
                BackHandler {
                    if (!navController.popBackStack()) {
                        finish()
                    }
                }
                AppNavGraph(navController = navController)
            }
        }
    }
}
