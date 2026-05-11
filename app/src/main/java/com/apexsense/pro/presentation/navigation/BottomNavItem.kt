package com.apexsense.pro.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Library : BottomNavItem(Screen.GameLibrary.route, "Vault", Icons.Filled.GridView)
    object Home : BottomNavItem(Screen.Home.route, "Boost", Icons.Filled.FlashOn)
    object Tools : BottomNavItem(Screen.GameTools.route, "Game Tools", Icons.Filled.Handyman)
}
