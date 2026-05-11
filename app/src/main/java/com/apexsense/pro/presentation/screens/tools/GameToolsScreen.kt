package com.apexsense.pro.presentation.screens.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.apexsense.pro.presentation.theme.AccentOrange
import com.apexsense.pro.presentation.theme.DarkBackground
import com.apexsense.pro.presentation.theme.SurfaceGray
import com.apexsense.pro.presentation.navigation.Screen
import com.apexsense.pro.service.CrosshairState
import com.apexsense.pro.service.OverlayService
import android.content.Intent
import androidx.compose.ui.platform.LocalContext

@Composable
fun GameToolsScreen(navController: NavController) {
    var width by remember { mutableStateOf("1080") }
    var height by remember { mutableStateOf("2436") }
    var smallestWidth by remember { mutableStateOf("432") }
    
    var crosshairEnabled by remember { mutableStateOf(false) }
    var monitorEnabled by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Game Tools",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Text("By B Word", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            item {
                ToolCard(
                    title = "Pengubah Resolusi",
                    subtitle = "Ubah resolusi layar (Lebar x Tinggi)",
                    icon = Icons.Filled.Refresh
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ModernInput(
                            value = width,
                            onValueChange = { width = it },
                            modifier = Modifier.weight(1f)
                        )
                        Text("×", color = Color.Gray, fontSize = 24.sp)
                        ModernInput(
                            value = height,
                            onValueChange = { height = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    AppliedButton()
                }
            }

            item {
                ToolCard(
                    title = "Pengubah Lebar Terkecil",
                    subtitle = "Timpa Lebar Terkecil (Smallest Width) ke perangkatmu secara langsung",
                    icon = Icons.Filled.SettingsSystemDaydream
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Texture, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            smallestWidth,
                            color = Color.White,
                            modifier = Modifier.weight(1f),
                            fontSize = 18.sp
                        )
                        Text("dp", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    AppliedButton()
                }
            }

            item {
                ActionToolItem(
                    title = "Sensitivity Engine",
                    subtitle = "Automated sensitivity calculation based on device specs",
                    onClick = { navController.navigate(Screen.SensitivityEngine.route) },
                    icon = Icons.Filled.Calculate
                )
            }

            item {
                Column {
                    val context = LocalContext.current
                    ToggleToolItem(
                        title = "Crosshair",
                        subtitle = "Titik referensi di layar untuk penyelarasan tampilan",
                        checked = crosshairEnabled,
                        onCheckedChange = { 
                            crosshairEnabled = it
                            val intent = Intent(context, OverlayService::class.java).apply {
                                action = if (it) "SHOW_CROSSHAIR" else "HIDE_CROSSHAIR"
                            }
                            context.startService(intent)
                        },
                        icon = Icons.Filled.FilterCenterFocus
                    )
                    
                    if (crosshairEnabled) {
                        CrosshairConfigArea()
                    }
                }
            }

            item {
                Column {
                    ToggleToolItem(
                        title = "Monitor Sesi",
                        subtitle = "Pantau performa perangkat saat kamu bermain",
                        checked = monitorEnabled,
                        onCheckedChange = { monitorEnabled = it },
                        icon = Icons.Filled.Analytics
                    )
                    
                    if (monitorEnabled) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceGray.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("DATA MONITOR", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    MonitorChip("FPS", true)
                                    MonitorChip("CPU", true)
                                    MonitorChip("GPU", false)
                                    MonitorChip("RAM", true)
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Interval Update: 1.0s", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(120.dp)) }
        }
    }
}

@Composable
fun ToolCard(title: String, subtitle: String, icon: ImageVector, content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(subtitle, color = Color.Gray, fontSize = 12.sp)
                }
                Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
fun ModernInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier) {
    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AppliedButton() {
    Button(
        onClick = { },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text("Diterapkan", color = Color.Gray)
    }
}

@Composable
fun ActionToolItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    icon: ImageVector
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, color = Color.Gray, fontSize = 11.sp)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun CrosshairConfigArea() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151210)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                // Left Styles
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CrosshairStyleIcon(Icons.Filled.Add, CrosshairState.style == Icons.Filled.Add) { CrosshairState.style = Icons.Filled.Add }
                    CrosshairStyleIcon(Icons.Filled.FilterCenterFocus, CrosshairState.style == Icons.Filled.FilterCenterFocus) { CrosshairState.style = Icons.Filled.FilterCenterFocus }
                    CrosshairStyleIcon(Icons.Filled.HorizontalRule, CrosshairState.style == Icons.Filled.HorizontalRule) { CrosshairState.style = Icons.Filled.HorizontalRule }
                    CrosshairStyleIcon(Icons.Filled.Circle, CrosshairState.style == Icons.Filled.Circle) { CrosshairState.style = Icons.Filled.Circle }
                    CrosshairStyleIcon(Icons.Filled.Adjust, CrosshairState.style == Icons.Filled.Adjust) { CrosshairState.style = Icons.Filled.Adjust }
                }

                // Center Positioner
                Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
                    // Joystick Outer Ring
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.05f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                        )
                    }
                    
                    // Center Visual
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(CrosshairState.style, contentDescription = null, tint = CrosshairState.color.copy(alpha = CrosshairState.alpha), modifier = Modifier.size((24 * CrosshairState.size).dp))
                        Text("0°", color = Color.Gray, fontSize = 12.sp)
                    }
                    
                    // Coordinates Label
                    Text("X: 0\nY: 0", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.align(Alignment.TopStart).padding(8.dp))
                }

                // Right Size Slider
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Size", color = Color.Gray, fontSize = 10.sp)
                    Text("${String.format("%.1fx", CrosshairState.size)}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.height(120.dp).width(40.dp)) {
                        Slider(
                            value = CrosshairState.size,
                            onValueChange = { CrosshairState.size = it },
                            valueRange = 0.5f..2.5f,
                            modifier = Modifier.align(Alignment.Center).rotate(-90f),
                            colors = SliderDefaults.colors(thumbColor = AccentOrange, activeTrackColor = AccentOrange)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Alpha Slider
            Column {
                Text("Alpha ${ (CrosshairState.alpha * 100).toInt() }%", color = Color.Gray, fontSize = 10.sp)
                Slider(
                    value = CrosshairState.alpha,
                    onValueChange = { CrosshairState.alpha = it },
                    colors = SliderDefaults.colors(thumbColor = AccentOrange, activeTrackColor = AccentOrange)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Color Palette
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val colors = listOf(Color.Red, Color(0xFFFF8C00), Color.White, Color.Yellow, Color.Blue, Color.Cyan)
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color, RoundedCornerShape(12.dp))
                            .border(if (CrosshairState.color == color) 2.dp else 0.dp, Color.White, RoundedCornerShape(12.dp))
                            .clickable { CrosshairState.color = color }
                    )
                }
            }
        }
    }
}

@Composable
fun CrosshairStyleIcon(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .background(
                if (isSelected) AccentOrange.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.03f),
                RoundedCornerShape(12.dp)
            )
            .border(1.dp, if (isSelected) AccentOrange else Color.Transparent, RoundedCornerShape(12.dp))
    ) {
        Icon(icon, contentDescription = null, tint = if (isSelected) AccentOrange else Color.Gray, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun ToggleToolItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, color = Color.Gray, fontSize = 11.sp)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AccentOrange,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Composable
fun MonitorChip(label: String, selected: Boolean) {
    Surface(
        color = if (selected) AccentOrange.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) AccentOrange else Color.Transparent)
    ) {
        Text(
            text = label,
            color = if (selected) AccentOrange else Color.Gray,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ColorDot(color: Color) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(color, androidx.compose.foundation.shape.CircleShape)
            .border(2.dp, Color.White.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape)
    )
}

// Add these to avoid unresolved imports in BasicTextField
@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    textStyle: androidx.compose.ui.text.TextStyle,
    modifier: Modifier
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        modifier = modifier
    )
}
