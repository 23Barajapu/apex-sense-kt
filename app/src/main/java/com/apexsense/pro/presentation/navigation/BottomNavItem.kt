package com.apexsense.pro.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Profile : BottomNavItem(Screen.Profile.route, "Profile", Icons.Filled.Person)
    object Home : BottomNavItem(Screen.Home.route, "Home", Icons.Filled.Home)
    object Tools : BottomNavItem(Screen.GameTools.route, "Game Tools", Icons.Filled.Handyman)
}
