package com.example.plantify.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape // <-- Import baru
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.plantify.ui.theme.PlantifyTheme

// Data class untuk item navigasi
data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String // Menambahkan route untuk navigasi nyata
)

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    val items = listOf(
        NavItem("Home", Icons.Default.Home, "home_route"),
        NavItem("Scan", Icons.Default.QrCodeScanner, "scan_route"), // Ini akan menjadi FAB
        NavItem("Profile", Icons.Default.Person, "profile_route")
    )

    // State untuk melacak item mana yang sedang dipilih
    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),

        // --- Perubahan untuk FAB "Terbang" ---
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Aksi Scan di sini */
                    // Saat FAB diklik, kita bisa langsung lompat ke layar scan
                     selectedItemIndex = 1 // Atur index ke "Scan" jika perlu mengubah state
                },
                shape = CircleShape, // Bentuk bulat untuk FAB
                // Warna FAB bisa disesuaikan
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.QrCodeScanner, "Scan Tanaman")
            }
        },
        // Posisi FAB di tengah bawah
        floatingActionButtonPosition = FabPosition.Center,
        // ------------------------------------

        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    // Khusus untuk item "Scan" (indeks 1), kita tidak ingin dia ada di NavigationBar
                    // Karena sudah jadi FAB
                    if (item.route == "scan_route") {
                        // Tidak ada Navigasi Item di sini untuk Scan
                        // Tapi kita bisa sisakan ruang kosong agar FAB terlihat di tengah
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        NavigationBarItem(
                            selected = (selectedItemIndex == index),
                            onClick = { selectedItemIndex = index }, // Update state
                            label = { Text(text = item.label) },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            alwaysShowLabel = true
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        // Konten utama layar (akan ditampilkan berdasarkan selectedItemIndex)
        Column(
            modifier = Modifier
                .padding(innerPadding) // <-- JANGAN LUPA PADDING!
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (selectedItemIndex) {
                0 -> Text(text = "Konten Layar HOME (Koleksi Tanamanku)")
                // Di sini, Anda mungkin ingin langsung membuka kamera atau ke layar scan
                // saat FAB diklik, bukan menampilkan teks "Konten Layar SCAN"
                1 -> Text(text = "Konten Layar SCAN (Akan diganti aksi buka kamera)")
                2 -> Text(text = "Konten Layar PROFILE (Pengaturan Akun)")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreen() {
    PlantifyTheme {
        HomeScreen()
    }
}