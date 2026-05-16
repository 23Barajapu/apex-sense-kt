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
import io.ktor.client.request.get
import io.ktor.client.call.body
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation

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
                var permissionTick by remember { mutableStateOf(0) }
                
                // Automatic polling to check permissions every 500ms while dialog is shown
                LaunchedEffect(showPermissionDialog) {
                    if (showPermissionDialog) {
                        while (true) {
                            val isOverlayGranted = Settings.canDrawOverlays(context)
                            val isWriteGranted = android.provider.Settings.System.canWrite(context)
                            
                            if (isOverlayGranted && isWriteGranted) {
                                showPermissionDialog = false
                                break
                            }
                            
                            // Trigger recomposition
                            permissionTick++
                            
                            kotlinx.coroutines.delay(500)
                        }
                    }
                }
                
                // Add Lifecycle observer to refresh permission status when returning to app
                val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner) {
                    val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                        if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                            showPermissionDialog = !Settings.canDrawOverlays(context) || !android.provider.Settings.System.canWrite(context)
                            permissionTick++
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                var showUpdateDialog by remember { mutableStateOf(false) }
                var updateUrl by remember { mutableStateOf("") }
                
                // --- VERSION CHECK LOGIC ---
                LaunchedEffect(Unit) {
                    try {
                        val client = io.ktor.client.HttpClient(io.ktor.client.engine.android.Android) {
                            install(ContentNegotiation) {
                                json(kotlinx.serialization.json.Json {
                                    ignoreUnknownKeys = true
                                })
                            }
                        }
                        
                        // Mengecek versi terbaru dari repositori kamu
                        val remoteInfo: Map<String, kotlinx.serialization.json.JsonElement> = 
                            client.get("https://raw.githubusercontent.com/23Barajapu/apex-sense-kt/main/update.json").body()
                        
                        val latestCode = remoteInfo["latest_version_code"]?.toString()?.toIntOrNull() ?: 1
                        val downloadLink = remoteInfo["update_url"]?.toString()?.replace("\"", "") ?: ""
                        
                        if (latestCode > 1) { // 1 adalah versionCode saat ini di build.gradle.kts
                            updateUrl = downloadLink
                            showUpdateDialog = true
                        }
                        client.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                if (showUpdateDialog) {
                    AlertDialog(
                        onDismissRequest = { /* Force update - cannot dismiss */ },
                        containerColor = Color(0xFF1A1614),
                        title = { Text("Update Versi Baru!", color = Color.White, fontWeight = FontWeight.Bold) },
                        text = { 
                            Text(
                                "Versi terbaru ApexSense sudah tersedia. Kamu harus melakukan update untuk terus menggunakan aplikasi ini.",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = com.apexsense.presentation.theme.AccentOrange),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("DOWNLOAD SEKARANG", fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }

                if (showPermissionDialog && !showUpdateDialog) {
                    // Force re-evaluation by reading permissionTick
                    if (permissionTick >= 0) { } 
                    
                    val needsOverlay = !Settings.canDrawOverlays(context)
                    val needsWriteSettings = !android.provider.Settings.System.canWrite(context)

                    if (needsOverlay) {
                        // LAYER 1: OVERLAY
                        AlertDialog(
                            onDismissRequest = { /* Don't dismiss */ },
                            containerColor = Color(0xFF1A1614),
                            title = { Text("Izin Overlay", color = Color.White, fontWeight = FontWeight.Bold) },
                            text = { 
                                Text(
                                    "ApexSense perlu izin 'Tampilkan di Atas Aplikasi Lain' agar Crosshair dan Monitor bisa muncul saat kamu bermain game.",
                                    color = Color.White,
                                    fontSize = 14.sp
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
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = com.apexsense.presentation.theme.AccentOrange),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("AKTIFKAN OVERLAY", fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                    } else if (needsWriteSettings) {
                        // LAYER 2: SYSTEM SETTINGS
                        AlertDialog(
                            onDismissRequest = { /* Don't dismiss */ },
                            containerColor = Color(0xFF1A1614),
                            title = { Text("Izin Tahap 2: Sistem", color = Color.White, fontWeight = FontWeight.Bold) },
                            text = { 
                                Text(
                                    "ApexSense perlu izin 'Ubah Setelan Sistem' agar fitur Pengubah Resolusi (DPI) bisa bekerja.",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        val intent = Intent(
                                            Settings.ACTION_MANAGE_WRITE_SETTINGS,
                                            Uri.parse("package:${context.packageName}")
                                        )
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = com.apexsense.presentation.theme.AccentOrange),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("AKTIFKAN IZIN SISTEM", fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                    } else {
                        showPermissionDialog = false
                    }
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
