package com.apexsense.pro.presentation.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.apexsense.pro.presentation.navigation.Screen
import com.apexsense.pro.presentation.theme.*

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.startMonitoring(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Subtle Background Glow
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = (-100).dp)
                .background(AccentOrange.copy(alpha = 0.1f), CircleShape)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(20.dp)) }

            item {
                HeaderSection()
            }

            item {
                HardwareMonitorPro(state)
            }

            item {
                Text(
                    "QUICK ACCESS",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ModernActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Game Library",
                        icon = Icons.Filled.Games,
                        onClick = { navController.navigate(Screen.GameLibrary.route) }
                    )
                    ModernActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Performance",
                        icon = Icons.Filled.History,
                        onClick = { navController.navigate(Screen.HardwareHistory.route) }
                    )
                }
            }
            
            item {
                Text(
                    "SYSTEM HEALTH",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatusWidget(
                        modifier = Modifier.weight(1f),
                        label = "RAM USAGE",
                        value = "2.4 GB",
                        icon = Icons.Filled.Memory
                    )
                    StatusWidget(
                        modifier = Modifier.weight(1f),
                        label = "STORAGE",
                        value = "85%",
                        icon = Icons.Filled.Storage
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(120.dp)) } 
        }
    }
}

@Composable
fun StatusWidget(modifier: Modifier, label: String, value: String, icon: ImageVector) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(label, color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp, 24.dp)
                    .background(AccentOrange, RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "APEXSENSE",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
        Text(
            text = "OPTIMIZED FOR PERFORMANCE",
            color = Color.Gray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}

@Composable
fun HardwareMonitorPro(state: HomeState) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "HARDWARE STATUS",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    state.deviceModel.uppercase(),
                    color = AccentOrange,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CircularMetric(
                    label = "CPU LOAD",
                    value = state.cpuUsage,
                    suffix = "%",
                    progress = state.cpuUsage / 100f
                )
                CircularMetric(
                    label = "TEMP",
                    value = state.temperature.toInt(),
                    suffix = "°C",
                    progress = (state.temperature / 100f).toFloat()
                )
            }
        }
    }
}

@Composable
fun CircularMetric(label: String, value: Int, suffix: String, progress: Float) {
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(1000))
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.size(80.dp),
                color = Color.White.copy(alpha = 0.05f),
                strokeWidth = 6.dp
            )
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.size(80.dp),
                color = AccentOrange,
                strokeWidth = 6.dp,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$value",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = suffix,
                    color = Color.Gray,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(bottom = 2.dp, start = 1.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(label, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensitivitySearchSection(
    width: String,
    height: String,
    onWidthChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onGenerate: () -> Unit
) {
    Column {
        Text(
            "SENSITIVITY ENGINE",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceGray),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = width,
                        onValueChange = onWidthChange,
                        label = { Text("Width", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentOrange,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = height,
                        onValueChange = onHeightChange,
                        label = { Text("Height", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentOrange,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onGenerate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CALCULATE NOW", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ModernActionCard(modifier: Modifier, title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(24.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f))
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(AccentOrange.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(20.dp))
                }
                Text(
                    title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
