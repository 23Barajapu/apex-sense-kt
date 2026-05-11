package com.apexsense.pro.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object SensitivityResult : Screen("result/{width}/{height}") {
        fun createRoute(width: Int, height: Int) = "result/$width/$height"
    }
    object GameLibrary : Screen("library")
    object HardwareHistory : Screen("history")
    object GameTools : Screen("tools")
    object Profile : Screen("profile")
    object SensitivityEngine : Screen("engine")
}
