package com.apexsense.pro.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.apexsense.pro.presentation.navigation.Screen
import com.apexsense.pro.presentation.theme.AccentOrange
import com.apexsense.pro.presentation.theme.CardGray
import com.apexsense.pro.presentation.theme.DarkBackground
import com.apexsense.pro.presentation.theme.SurfaceGray

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    
    var width by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.startMonitoring(context)
    }

    Scaffold(
        containerColor = DarkBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "ApexSense Pro",
                    color = AccentOrange,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Lead Android Architect Edition",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            item {
                HardwareMonitorCard(state)
            }

            item {
                SensitivityInputCard(
                    width = width,
                    height = height,
                    onWidthChange = { width = it },
                    onHeightChange = { height = it },
                    onGenerate = {
                        if (width.isNotEmpty() && height.isNotEmpty()) {
                            navController.navigate(Screen.SensitivityResult.createRoute(width.toInt(), height.toInt()))
                        }
                    }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Game Library",
                        icon = Icons.Filled.Games,
                        onClick = { navController.navigate(Screen.GameLibrary.route) }
                    )
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        title = "History",
                        icon = Icons.Filled.History,
                        onClick = { navController.navigate(Screen.HardwareHistory.route) }
                    )
                }
            }
        }
    }
}

@Composable
fun HardwareMonitorCard(state: HomeState) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Real-time Monitor", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MonitorItem("CPU Load", "${state.cpuUsage}%")
                MonitorItem("Battery Temp", "${state.temperature}°C")
                MonitorItem("Device", state.deviceModel.take(12))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensitivityInputCard(
    width: String,
    height: String,
    onWidthChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onGenerate: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Search Sensitivity", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = width,
                    onValueChange = onWidthChange,
                    placeholder = { Text("Width", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CardGray,
                        unfocusedContainerColor = CardGray,
                        focusedIndicatorColor = AccentOrange
                    )
                )
                TextField(
                    value = height,
                    onValueChange = onHeightChange,
                    placeholder = { Text("Height", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CardGray,
                        unfocusedContainerColor = CardGray,
                        focusedIndicatorColor = AccentOrange
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onGenerate,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Recommendation")
            }
        }
    }
}

@Composable
fun QuickActionCard(modifier: Modifier, title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
fun MonitorItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.Gray, fontSize = 10.sp)
        Text(value, color = AccentOrange, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
