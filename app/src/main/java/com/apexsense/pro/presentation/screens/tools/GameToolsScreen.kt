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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.clickable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.apexsense.pro.presentation.theme.AccentOrange
import com.apexsense.pro.presentation.theme.DarkBackground
import com.apexsense.pro.presentation.theme.SurfaceGray
import com.apexsense.pro.presentation.navigation.Screen
import com.apexsense.pro.service.CrosshairState
import com.apexsense.pro.service.AppMonitorState
import com.apexsense.pro.service.MonitorConfig
import com.apexsense.pro.service.OverlayService
import android.content.Intent
import androidx.compose.ui.platform.LocalContext

@Composable
fun GameToolsScreen(navController: NavController) {
    val context = LocalContext.current
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
                        onCheckedChange = { 
                            monitorEnabled = it
                            val intent = Intent(context, OverlayService::class.java).apply {
                                action = if (it) "SHOW_MONITOR" else "HIDE_MONITOR"
                            }
                            context.startService(intent)
                        },
                        icon = Icons.Filled.Analytics
                    )
                    
                    if (monitorEnabled) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF151210)),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            val monitorConfig by AppMonitorState.config.collectAsState()
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                MonitorToggleItem("CPU Information", monitorConfig.showCpu) { AppMonitorState.update { c: com.apexsense.pro.service.MonitorConfig -> c.copy(showCpu = !c.showCpu) } }
                                MonitorToggleItem("GPU Information", monitorConfig.showGpu) { AppMonitorState.update { c: com.apexsense.pro.service.MonitorConfig -> c.copy(showGpu = !c.showGpu) } }
                                MonitorToggleItem("RAM Information", monitorConfig.showRam) { AppMonitorState.update { c: com.apexsense.pro.service.MonitorConfig -> c.copy(showRam = !c.showRam) } }
                                MonitorToggleItem("Battery Information", monitorConfig.showBattery) { AppMonitorState.update { c: com.apexsense.pro.service.MonitorConfig -> c.copy(showBattery = !c.showBattery) } }
                                MonitorToggleItem("Temperature Information", monitorConfig.showTemp) { AppMonitorState.update { c: com.apexsense.pro.service.MonitorConfig -> c.copy(showTemp = !c.showTemp) } }
                                MonitorToggleItem("FPS Information", monitorConfig.showFps) { AppMonitorState.update { c: com.apexsense.pro.service.MonitorConfig -> c.copy(showFps = !c.showFps) } }
                                MonitorToggleItem("Time Information", monitorConfig.showTime) { AppMonitorState.update { c: com.apexsense.pro.service.MonitorConfig -> c.copy(showTime = !c.showTime) } }
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
    val config by CrosshairState.config.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151210)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                // Left Styles
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CrosshairStyleIcon(Icons.Filled.Add, config.style == Icons.Filled.Add) { CrosshairState.update { it.copy(style = Icons.Filled.Add) } }
                    CrosshairStyleIcon(Icons.Filled.FilterCenterFocus, config.style == Icons.Filled.FilterCenterFocus) { CrosshairState.update { it.copy(style = Icons.Filled.FilterCenterFocus) } }
                    CrosshairStyleIcon(Icons.Filled.HorizontalRule, config.style == Icons.Filled.HorizontalRule) { CrosshairState.update { it.copy(style = Icons.Filled.HorizontalRule) } }
                    CrosshairStyleIcon(Icons.Filled.Circle, config.style == Icons.Filled.Circle) { CrosshairState.update { it.copy(style = Icons.Filled.Circle) } }
                    CrosshairStyleIcon(Icons.Filled.Adjust, config.style == Icons.Filled.Adjust) { CrosshairState.update { it.copy(style = Icons.Filled.Adjust) } }
                }

                // Center Positioner
                Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
                    // Joystick Outer Ring
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.05f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                        )
                    }
                    
                    // Coordinates Label
                    Text("X: 0\nY: 0", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.align(Alignment.TopStart).padding(8.dp))
                }

                // Right Size Slider
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Size", color = Color.Gray, fontSize = 10.sp)
                    Text("${String.format("%.1fx", config.size)}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.height(200.dp).width(40.dp)) {
                        Slider(
                            value = config.size,
                            onValueChange = { newSize -> CrosshairState.update { it.copy(size = newSize) } },
                            valueRange = 0.5f..2.5f,
                            modifier = Modifier.align(Alignment.Center).rotate(-90f).width(200.dp),
                            colors = SliderDefaults.colors(thumbColor = AccentOrange, activeTrackColor = AccentOrange)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Alpha Slider
            Column {
                Text("Alpha ${ (config.alpha * 100).toInt() }%", color = Color.Gray, fontSize = 10.sp)
                Slider(
                    value = config.alpha,
                    onValueChange = { newAlpha -> CrosshairState.update { it.copy(alpha = newAlpha) } },
                    colors = SliderDefaults.colors(thumbColor = AccentOrange, activeTrackColor = AccentOrange)
                )
            }

            // Rotation Controls
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Rotation ${ config.rotation.toInt() }°", color = Color.Gray, fontSize = 10.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SmallStepButton(Icons.Default.Remove) { 
                            CrosshairState.update { it.copy(rotation = (it.rotation - 1f).let { r -> if (r < 0) r + 360 else r }) } 
                        }
                        SmallStepButton(Icons.Default.Add) { 
                            CrosshairState.update { it.copy(rotation = (it.rotation + 1f) % 360) } 
                        }
                    }
                }
                Slider(
                    value = config.rotation,
                    onValueChange = { newRot -> CrosshairState.update { it.copy(rotation = newRot) } },
                    valueRange = 0f..360f,
                    colors = SliderDefaults.colors(thumbColor = AccentOrange, activeTrackColor = AccentOrange)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf(0f, 45f, 90f, 135f, 180f)
                    presets.forEach { angle ->
                        AngleChip(
                            label = "${angle.toInt()}°",
                            isSelected = config.rotation == angle,
                            modifier = Modifier.weight(1f)
                        ) {
                            CrosshairState.update { it.copy(rotation = angle) }
                        }
                    }
                }
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
                            .border(if (config.color == color) 2.dp else 0.dp, Color.White, RoundedCornerShape(12.dp))
                            .clickable { CrosshairState.update { it.copy(color = color) } }
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

@Composable
fun SmallStepButton(icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.size(28.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun AngleChip(label: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) AccentOrange.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.03f),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AccentOrange else Color.Transparent),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = if (isSelected) AccentOrange else Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun MonitorToggleItem(label: String, isChecked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    if (isChecked) AccentOrange else Color.White.copy(alpha = 0.05f),
                    RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
            }
        }
    }
}
