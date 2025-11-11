package com.example.plantify.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // <-- Import baru
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.plantify.ui.theme.PlantifyTheme
import kotlinx.coroutines.delay // <-- Import baru

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Login()
    }
}

@Composable
fun Login() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // --- PERUBAHAN DI SINI UNTUK EFEK TEKS ---
    val phrases = remember { listOf(
        "Selamat Datang!",
        "Scan Tanamanmu!",
        "Selalu Ingat Plantify!"
    )}
    var currentPhraseIndex by remember { mutableStateOf(0) }
    var displayedText by remember { mutableStateOf("") }
    var currentCharIndex by remember { mutableStateOf(0) }

    LaunchedEffect(key1 = currentPhraseIndex) {
        val currentPhrase = phrases[currentPhraseIndex]
        currentCharIndex = 0
        displayedText = ""

        // Efek ketik
        for (i in currentPhrase.indices) {
            displayedText = currentPhrase.substring(0, i + 1)
            delay(80)
        }

        delay(1000)

        // Efek hapus (opsional, jika ingin teks terhapus sebelum ganti)
         for (i in currentPhrase.length downTo 0) {
             displayedText = currentPhrase.substring(0, i)
             delay(50) // Jeda hapus
         }
         delay(500)

        currentPhraseIndex = (currentPhraseIndex + 1) % phrases.size
    }
    // ------------------------------------------

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = displayedText,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: Logika login di sini */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PlantifyTheme {
        LoginScreen()
    }
}