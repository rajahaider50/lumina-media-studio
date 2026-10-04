package com.example.premiumapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.premiumapp.core.navigation.LuminaNavGraph
import com.example.premiumapp.design.theme.LuminaTheme
import com.example.premiumapp.domain.model.UserPreferences

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val app = application as App
        val container = app.container

        setContent {
            val preferences by container.preferencesRepository.getUserPreferences().collectAsState(initial = UserPreferences())

            LuminaTheme(
                themeMode = preferences.themeMode,
                dynamicColor = preferences.dynamicColor,
                reduceMotion = preferences.reduceMotion
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    LuminaNavGraph(
                        navController = navController,
                        container = container
                    )
                }
            }
        }
    }
}
