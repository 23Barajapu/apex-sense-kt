package com.apexsense.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.apexsense.pro.presentation.components.ApexBottomBar
import com.apexsense.pro.presentation.navigation.Screen
import com.apexsense.pro.presentation.screens.engine.SensitivityEngineScreen
import com.apexsense.pro.presentation.screens.history.HistoryScreen
import com.apexsense.pro.presentation.screens.home.HomeScreen
import com.apexsense.pro.presentation.screens.library.LibraryScreen
import com.apexsense.pro.presentation.screens.profile.ProfileScreen
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
                val context = LocalContext.current
                
                var showPermissionDialog by remember { 
                    mutableStateOf(!Settings.canDrawOverlays(context)) 
                }

                if (showPermissionDialog) {
                    AlertDialog(
                        onDismissRequest = { /* Don't dismiss without action */ },
                        containerColor = Color(0xFF1A1614),
                        title = { Text("Permission Required", color = Color.White, fontWeight = FontWeight.Bold) },
                        text = { 
                            Text(
                                "ApexSense needs 'Display Over Other Apps' permission to show the gaming crosshair and performance monitor while you are playing.",
                                color = Color.Gray
                            ) 
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    val intent = Intent(
                                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:${context.packageName}")
                                    )
                                    context.startActivity(intent)
                                    showPermissionDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = com.apexsense.pro.presentation.theme.AccentOrange),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("GRANT PERMISSION", fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }

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
                                GameToolsScreen(navController = navController)
                            }
                            composable(Screen.Profile.route) {
                                ProfileScreen()
                            }
                            composable(Screen.SensitivityEngine.route) {
                                SensitivityEngineScreen(navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}
