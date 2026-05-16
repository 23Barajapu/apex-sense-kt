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
import androidx.navigation.compose.currentBackStackEntryAsState
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
                val items = listOf(
                    com.apexsense.pro.presentation.navigation.BottomNavItem.Profile,
                    com.apexsense.pro.presentation.navigation.BottomNavItem.Home,
                    com.apexsense.pro.presentation.navigation.BottomNavItem.Tools
                )
                val pagerState = androidx.compose.foundation.pager.rememberPagerState(initialPage = 1) { items.size }
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

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = when {
                    currentRoute == Screen.Splash.route -> false
                    currentRoute == Screen.GameLibrary.route -> false
                    currentRoute == Screen.SensitivityEngine.route -> false
                    currentRoute?.startsWith("result") == true -> false
                    else -> currentRoute != null
                }

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            com.apexsense.pro.presentation.components.ApexBottomBar(
                                navController = navController,
                                pagerState = pagerState
                            )
                        }
                    },
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(innerPadding)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Splash.route,
                            enterTransition = { androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(400)) + androidx.compose.animation.slideInVertically(initialOffsetY = { -100 }) },
                            exitTransition = { androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(400)) + androidx.compose.animation.slideOutVertically(targetOffsetY = { 100 }) },
                            popEnterTransition = { androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(400)) + androidx.compose.animation.slideInVertically(initialOffsetY = { -100 }) },
                            popExitTransition = { androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(400)) + androidx.compose.animation.slideOutVertically(targetOffsetY = { 100 }) }
                        ) {
                            composable(Screen.Splash.route) {
                                SplashScreen(navController = navController)
                            }
                            composable(Screen.MainContainer.route) {
                                com.apexsense.pro.presentation.navigation.MainPagerScreen(
                                    navController = navController,
                                    pagerState = pagerState
                                )
                            }
                            composable(Screen.Home.route) {
                                // Redirect to MainContainer
                                LaunchedEffect(Unit) {
                                    pagerState.scrollToPage(1)
                                    navController.navigate(Screen.MainContainer.route) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                }
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
                                // Redirect to MainContainer
                                LaunchedEffect(Unit) {
                                    pagerState.scrollToPage(2)
                                    navController.navigate(Screen.MainContainer.route)
                                }
                            }
                            composable(Screen.Profile.route) {
                                // Redirect to MainContainer
                                LaunchedEffect(Unit) {
                                    pagerState.scrollToPage(0)
                                    navController.navigate(Screen.MainContainer.route)
                                }
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
