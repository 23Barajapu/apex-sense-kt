package com.apexsense

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.apexsense.presentation.components.ApexBottomBar
import com.apexsense.presentation.navigation.Screen
import com.apexsense.presentation.screens.engine.SensitivityEngineScreen
import com.apexsense.presentation.screens.history.HistoryScreen
import com.apexsense.presentation.screens.home.HomeScreen
import com.apexsense.presentation.screens.library.LibraryScreen
import com.apexsense.presentation.screens.profile.ProfileScreen
import com.apexsense.presentation.screens.result.ResultScreen
import com.apexsense.presentation.screens.splash.SplashScreen
import com.apexsense.presentation.screens.tools.GameToolsScreen
import com.apexsense.presentation.theme.ApexSenseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            ApexSenseTheme {
                val navController = rememberNavController()
                val items = listOf(
                    com.apexsense.presentation.navigation.BottomNavItem.Profile,
                    com.apexsense.presentation.navigation.BottomNavItem.Home,
                    com.apexsense.presentation.navigation.BottomNavItem.Tools
                )
                val pagerState = androidx.compose.foundation.pager.rememberPagerState(initialPage = 1) { items.size }
                val snackbarHostState = remember { SnackbarHostState() }
                val context = LocalContext.current
                
                var showPermissionDialog by remember { 
                    mutableStateOf(!Settings.canDrawOverlays(context) || !android.provider.Settings.System.canWrite(context)) 
                }

                // Add Lifecycle observer to refresh permission status when returning to app
                val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner) {
                    val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                        if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                            showPermissionDialog = !Settings.canDrawOverlays(context) || !android.provider.Settings.System.canWrite(context)
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                if (showPermissionDialog) {
                    val needsOverlay = !Settings.canDrawOverlays(context)
                    val needsWriteSettings = !android.provider.Settings.System.canWrite(context)

                    AlertDialog(
                        onDismissRequest = { /* Don't dismiss without action */ },
                        containerColor = Color(0xFF1A1614),
                        title = { Text("Permission Required", color = Color.White, fontWeight = FontWeight.Bold) },
                        text = { 
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    "ApexSense needs these permissions to function properly:",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                if (needsOverlay) {
                                    Text("• Display Over Other Apps (For Crosshair & Monitor)", color = Color.Gray, fontSize = 13.sp)
                                }
                                if (needsWriteSettings) {
                                    Text("• Modify System Settings (For Resolution Changer)", color = Color.Gray, fontSize = 13.sp)
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (needsOverlay) {
                                        val intent = Intent(
                                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                            Uri.parse("package:${context.packageName}")
                                        )
                                        context.startActivity(intent)
                                    } else if (needsWriteSettings) {
                                        val intent = Intent(
                                            Settings.ACTION_MANAGE_WRITE_SETTINGS,
                                            Uri.parse("package:${context.packageName}")
                                        )
                                        context.startActivity(intent)
                                    }
                                    
                                    // Re-check after returning (the dialog will re-evaluate on next recomposition if app resumed)
                                    showPermissionDialog = !Settings.canDrawOverlays(context) || !android.provider.Settings.System.canWrite(context)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = com.apexsense.presentation.theme.AccentOrange),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(if (needsOverlay) "GRANT OVERLAY" else "GRANT WRITE SETTINGS", fontWeight = FontWeight.Bold)
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
                            com.apexsense.presentation.components.ApexBottomBar(
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
                                com.apexsense.presentation.navigation.MainPagerScreen(
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
                            composable(Screen.DevOptionsGuide.route) {
                                com.apexsense.presentation.screens.tools.DevOptionsGuideScreen(navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}
