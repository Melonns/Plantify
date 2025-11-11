package com.example.plantify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.plantify.ui.theme.PlantifyTheme

// Data class (tidak berubah)
data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

// Daftar rute navigasi
object Routes {
    const val HOME = "home"
    const val SCAN = "scan"
    const val PROFILE = "profile"
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    val items = listOf(
        NavItem("Home", Icons.Default.Home, Routes.HOME),
        NavItem("Scan", Icons.Filled.PhotoCamera, Routes.SCAN),
        NavItem("Profile", Icons.Default.Person, Routes.PROFILE)
    )

    // 1. Buat NavController
    val navController = rememberNavController()

    // 2. KITA BISA HAPUS 'BackHandler' DARI SINI
    //    (Jetpack Navigation akan mengurusnya)

    Box(modifier = modifier.fillMaxSize()) {

        // 3. KONTEN UTAMA SEKARANG ADALAH NavHost
        Scaffold { innerPadding ->
            // 'innerPadding' dari Scaffold akan diterapkan
            // di dalam NavHost agar status bar tidak tertutup
            AppNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }

        // 4. Dapatkan rute saat ini dari NavController
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // 5. Sembunyikan bar HANYA JIKA rute saat ini adalah "scan"
        if (currentRoute == Routes.HOME ) {

            // "KOTAK" BAR (Home & Profile)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(64.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 64.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Item Home
                    CustomBottomBarItem(
                        item = items[0],
                        // 6. 'isSelected' dicek dari rute saat ini
                        isSelected = (currentRoute == Routes.HOME),
                        // 7. 'onClick' sekarang MENAVIGASI
                        onClick = {
                            navController.navigate(Routes.HOME) {
                                // Pop up ke start destination agar back stack tidak menumpuk
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )

                    // Item Profile
                    CustomBottomBarItem(
                        item = items[2],
                        // 6. 'isSelected' dicek dari rute saat ini
                        isSelected = (currentRoute == Routes.PROFILE),
                        // 7. 'onClick' sekarang MENAVIGASI
                        onClick = {
                            navController.navigate(Routes.PROFILE) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            // TOMBOL "SCAN"
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-32).dp)
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        // 7. 'onClick' sekarang MENAVIGASI
                        navController.navigate(Routes.SCAN) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = items[1].icon,
                    contentDescription = items[1].label,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

// Composable baru untuk NavHost (Pemisah Konten)
@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME, // Mulai dari 'home'
        modifier = modifier
    ) {
        // Rute untuk 'home' -> memanggil HomeContentScreen
        composable(Routes.HOME) {
            HomeContentScreen()
        }

        // Rute untuk 'scan' -> memanggil ScanScreen
        composable(Routes.SCAN) {
            ScanScreen()
        }

        // Rute untuk 'profile' -> memanggil ProfileScreen
        composable(Routes.PROFILE) {
            ProfileScreen()
        }
    }
}


// Composable helper (tidak berubah)
@Composable
fun CustomBottomBarItem(
    modifier: Modifier = Modifier,
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


// Preview (tidak berubah)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PlantifyTheme {
        HomeScreen()
    }
}