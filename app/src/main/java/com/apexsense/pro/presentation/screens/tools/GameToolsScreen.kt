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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apexsense.pro.presentation.theme.AccentOrange
import com.apexsense.pro.presentation.theme.DarkBackground
import com.apexsense.pro.presentation.theme.SurfaceGray

@Composable
fun GameToolsScreen() {
    var width by remember { mutableStateOf("1080") }
    var height by remember { mutableStateOf("2436") }
    var smallestWidth by remember { mutableStateOf("432") }
    
    var gyroEnabled by remember { mutableStateOf(false) }
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
                ToggleToolItem(
                    title = "Kalibrasi Gyro",
                    subtitle = "Panduan visual untuk penyelarasan giroskop selama permainan",
                    checked = gyroEnabled,
                    onCheckedChange = { gyroEnabled = it },
                    icon = Icons.Filled.Speed
                )
            }

            item {
                Column {
                    ToggleToolItem(
                        title = "Crosshair",
                        subtitle = "Titik referensi di layar untuk penyelarasan tampilan",
                        checked = crosshairEnabled,
                        onCheckedChange = { crosshairEnabled = it },
                        icon = Icons.Filled.FilterCenterFocus
                    )
                    
                    if (crosshairEnabled) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceGray.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("KONFIGURASI", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Warna", color = Color.White, fontSize = 14.sp)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        ColorDot(Color.Red)
                                        ColorDot(Color.Green)
                                        ColorDot(AccentOrange)
                                        ColorDot(Color.Cyan)
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text("Ukuran", color = Color.White, fontSize = 14.sp)
                                Slider(
                                    value = 0.5f,
                                    onValueChange = { },
                                    colors = SliderDefaults.colors(
                                        thumbColor = AccentOrange,
                                        activeTrackColor = AccentOrange
                                    )
                                )
                            }
                        }
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
