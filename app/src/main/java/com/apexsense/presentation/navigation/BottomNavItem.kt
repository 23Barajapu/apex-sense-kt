package com.apexsense.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Profile : BottomNavItem(Screen.Profile.route, "Profil", Icons.Filled.Person)
    object Home : BottomNavItem(Screen.Home.route, "Beranda", Icons.Filled.Home)
    object Tools : BottomNavItem(Screen.GameTools.route, "Alat Game", Icons.Filled.Handyman)
}
