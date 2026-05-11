package com.apexsense.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.apexsense.pro.presentation.components.ApexBottomBar
import com.apexsense.pro.presentation.navigation.Screen
import com.apexsense.pro.presentation.screens.history.HistoryScreen
import com.apexsense.pro.presentation.screens.home.HomeScreen
import com.apexsense.pro.presentation.screens.library.LibraryScreen
import com.apexsense.pro.presentation.screens.result.ResultScreen
import com.apexsense.pro.presentation.screens.splash.SplashScreen
import com.apexsense.pro.presentation.screens.tools.GameToolsScreen
import com.apexsense.pro.presentation.theme.ApexSenseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            ApexSenseTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(
                    bottomBar = {
                        ApexBottomBar(navController = navController)
                    },
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(innerPadding)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Splash.route
                        ) {
                            composable(Screen.Splash.route) {
                                SplashScreen(navController = navController)
                            }
                            composable(Screen.Home.route) {
                                HomeScreen(navController = navController)
                            }
                            composable(
                                route = Screen.SensitivityResult.route,
                                arguments = listOf(
                                    navArgument("width") { type = NavType.IntType },
                                    navArgument("height") { type = NavType.IntType }
                                )
                            ) { backStackEntry ->
                                val width = backStackEntry.arguments?.getInt("width") ?: 0
                                val height = backStackEntry.arguments?.getInt("height") ?: 0
                                ResultScreen(navController = navController, width = width, height = height)
                            }
                            composable(Screen.GameLibrary.route) {
                                LibraryScreen(navController = navController)
                            }
                            composable(Screen.HardwareHistory.route) {
                                HistoryScreen(navController = navController)
                            }
                            composable(Screen.GameTools.route) {
                                GameToolsScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}
