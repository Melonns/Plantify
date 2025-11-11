package com.example.plantify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.plantify.ui.screens.HomeScreen
import com.example.plantify.ui.theme.PlantifyTheme
import com.example.plantify.ui.screens.LoginScreen

// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlantifyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // TERAPKAN PADDING DI SINI
//                    LoginScreen(
//                        modifier = Modifier.padding(innerPadding)
//                    )
                    HomeScreen(modifer = Modifier.padding(innerPadding))
                }
            }
        }
    }
}