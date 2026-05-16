package com.apexsense.presentation.screens.tools

import androidx.compose.foundation.*
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
import com.apexsense.presentation.theme.AccentOrange
import com.apexsense.presentation.theme.DarkBackground
import com.apexsense.presentation.theme.SurfaceGray
import com.apexsense.presentation.navigation.Screen
import com.apexsense.service.CrosshairState
import com.apexsense.service.AppMonitorState
import com.apexsense.service.MonitorConfig
import com.apexsense.service.OverlayService
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.apexsense.presentation.components.CopyrightFooter
import com.apexsense.presentation.components.PageHeader

@Composable
fun GameToolsScreen(navController: NavController) {
    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    val configuration = context.resources.configuration
    
    var width by remember { mutableStateOf(displayMetrics.widthPixels.toString()) }
    var height by remember { mutableStateOf(displayMetrics.heightPixels.toString()) }
    var smallestWidth by remember { mutableStateOf(configuration.smallestScreenWidthDp.toString()) }
    
    var crosshairEnabled by remember { mutableStateOf(OverlayService.isCrosshairActive.value) }
    var monitorEnabled by remember { mutableStateOf(OverlayService.isMonitorActive.value) }

    // Sync with actual service state
    val crosshairActive by OverlayService.isCrosshairActive.collectAsState()
    val monitorActive by OverlayService.isMonitorActive.collectAsState()
    LaunchedEffect(crosshairActive) { crosshairEnabled = crosshairActive }
    LaunchedEffect(monitorActive) { monitorEnabled = monitorActive }

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
                PageHeader(title = "Tools")
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
                    Button(
                        onClick = {
                            val w = width.toIntOrNull() ?: displayMetrics.widthPixels
                            val h = height.toIntOrNull() ?: displayMetrics.heightPixels
                            
                            try {
                                // Attempting direct resolution change (Requires WRITE_SECURE_SETTINGS)
                                android.provider.Settings.Secure.putString(
                                    context.contentResolver,
                                    "display_size_forced",
                                    "${w}x${h}"
                                )
                                android.widget.Toast.makeText(context, "Resolusi diubah ke ${w}x${h}", android.widget.Toast.LENGTH_SHORT).show()
                            } catch (e: SecurityException) {
                                navController.navigate(Screen.DevOptionsGuide.route)
                                android.widget.Toast.makeText(context, "Butuh izin tambahan untuk ubah resolusi!", android.widget.Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                android.widget.Toast.makeText(context, "Gagal: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Terapkan Sekarang", color = Color.White, fontWeight = FontWeight.Bold)
                    }
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
                        BasicTextField(
                            value = smallestWidth,
                            onValueChange = { smallestWidth = it },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Text("dp", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val dpValue = smallestWidth.toIntOrNull() ?: 360
                            val widthPixels = displayMetrics.widthPixels
                            
                            // Calculate required density: (Pixels / DP) * 160
                            val newDensity = (widthPixels.toFloat() / dpValue.toFloat() * 160f).toInt()

                            try {
                                // Attempting direct change (Requires WRITE_SECURE_SETTINGS permission)
                                android.provider.Settings.Secure.putInt(
                                    context.contentResolver,
                                    "display_density_forced",
                                    newDensity
                                )
                                android.widget.Toast.makeText(context, "Berhasil! Lebar Terkecil menjadi $dpValue dp", android.widget.Toast.LENGTH_SHORT).show()
                            } catch (e: SecurityException) {
                                // If permission is missing, show the guide
                                navController.navigate(Screen.DevOptionsGuide.route)
                                android.widget.Toast.makeText(context, "Butuh izin tambahan! Ikuti panduan di sini.", android.widget.Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                android.widget.Toast.makeText(context, "Gagal: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Terapkan Sekarang", color = Color.White, fontWeight = FontWeight.Bold)
                    }
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
                                MonitorToggleItem("CPU Information", monitorConfig.showCpu) { AppMonitorState.update { c: com.apexsense.service.MonitorConfig -> c.copy(showCpu = !c.showCpu) } }
                                MonitorToggleItem("GPU Information", monitorConfig.showGpu) { AppMonitorState.update { c: com.apexsense.service.MonitorConfig -> c.copy(showGpu = !c.showGpu) } }
                                MonitorToggleItem("RAM Information", monitorConfig.showRam) { AppMonitorState.update { c: com.apexsense.service.MonitorConfig -> c.copy(showRam = !c.showRam) } }
                                MonitorToggleItem("Battery Information", monitorConfig.showBattery) { AppMonitorState.update { c: com.apexsense.service.MonitorConfig -> c.copy(showBattery = !c.showBattery) } }
                                MonitorToggleItem("Temperature Information", monitorConfig.showTemp) { AppMonitorState.update { c: com.apexsense.service.MonitorConfig -> c.copy(showTemp = !c.showTemp) } }
                                MonitorToggleItem("FPS Information", monitorConfig.showFps) { AppMonitorState.update { c: com.apexsense.service.MonitorConfig -> c.copy(showFps = !c.showFps) } }
                            }
                        }
                    }
                }
            }

            item {
                CopyrightFooter()
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
            // Header with Reset Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Customization", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                TextButton(
                    onClick = { CrosshairState.update { com.apexsense.service.CrosshairConfig() } },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Reset", color = AccentOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            val previewHeight = 220.dp
            Row(
                modifier = Modifier.fillMaxWidth().height(previewHeight),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Styles - Scrollable
                Box(modifier = Modifier.width(50.dp).fillMaxHeight()) {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CrosshairState.availableStyles.forEach { styleId ->
                            CrosshairStyleButton(
                                styleId = styleId, 
                                isSelected = config.style == styleId,
                                color = config.color
                            ) { 
                                CrosshairState.update { it.copy(style = styleId) } 
                            }
                        }
                    }
                }

                // Center Preview Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.White.copy(alpha = 0.02f), RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Ring
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.05f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                        )
                    }
                    
                    // Actual Crosshair Preview
                    androidx.compose.foundation.Canvas(
                        modifier = Modifier.size(48.dp).graphicsLayer { rotationZ = config.rotation }
                    ) {
                        val drawColor = config.color.copy(alpha = config.alpha)
                        val strokeWidth = config.thickness.dp.toPx()
                        val totalSize = size.width
                        val center = totalSize / 2
                        val halfLen = (totalSize / 2) * config.length
                        
                        when (config.style) {
                            "cross" -> {
                                drawLine(drawColor, androidx.compose.ui.geometry.Offset(center, center - halfLen), androidx.compose.ui.geometry.Offset(center, center + halfLen), strokeWidth)
                                drawLine(drawColor, androidx.compose.ui.geometry.Offset(center - halfLen, center), androidx.compose.ui.geometry.Offset(center + halfLen, center), strokeWidth)
                            }
                            "dot" -> {
                                drawCircle(drawColor, radius = (totalSize / 6) * config.length)
                            }
                            "circle" -> {
                                drawCircle(drawColor, radius = halfLen, style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth))
                            }
                            "gap_cross" -> {
                                val gap = (totalSize / 4)
                                val lineLen = halfLen
                                drawLine(drawColor, androidx.compose.ui.geometry.Offset(center, center - gap - lineLen), androidx.compose.ui.geometry.Offset(center, center - gap), strokeWidth)
                                drawLine(drawColor, androidx.compose.ui.geometry.Offset(center, center + gap), androidx.compose.ui.geometry.Offset(center, center + gap + lineLen), strokeWidth)
                                drawLine(drawColor, androidx.compose.ui.geometry.Offset(center - gap - lineLen, center), androidx.compose.ui.geometry.Offset(center - gap, center), strokeWidth)
                                drawLine(drawColor, androidx.compose.ui.geometry.Offset(center + gap, center), androidx.compose.ui.geometry.Offset(center + gap + lineLen, center), strokeWidth)
                            }
                            "square" -> {
                                drawRect(
                                    drawColor, 
                                    topLeft = androidx.compose.ui.geometry.Offset(center - halfLen, center - halfLen),
                                    size = androidx.compose.ui.geometry.Size(halfLen * 2, halfLen * 2),
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth)
                                )
                            }
                            "t_shape" -> {
                                drawLine(drawColor, androidx.compose.ui.geometry.Offset(center, center), androidx.compose.ui.geometry.Offset(center, center + halfLen), strokeWidth)
                                drawLine(drawColor, androidx.compose.ui.geometry.Offset(center - halfLen, center), androidx.compose.ui.geometry.Offset(center + halfLen, center), strokeWidth)
                            }
                            "chevron" -> {
                                drawLine(drawColor, androidx.compose.ui.geometry.Offset(center - halfLen, center + halfLen), androidx.compose.ui.geometry.Offset(center, center), strokeWidth)
                                drawLine(drawColor, androidx.compose.ui.geometry.Offset(center + halfLen, center + halfLen), androidx.compose.ui.geometry.Offset(center, center), strokeWidth)
                            }
                            "x_shape" -> {
                                drawLine(drawColor, androidx.compose.ui.geometry.Offset(center - halfLen, center - halfLen), androidx.compose.ui.geometry.Offset(center + halfLen, center + halfLen), strokeWidth)
                                drawLine(drawColor, androidx.compose.ui.geometry.Offset(center + halfLen, center - halfLen), androidx.compose.ui.geometry.Offset(center - halfLen, center + halfLen), strokeWidth)
                            }
                        }
                    }

                    // Joystick Handle representation
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .size(12.dp)
                            .background(AccentOrange, androidx.compose.foundation.shape.CircleShape)
                    )

                    // Coordinates Label
                    Text(
                        "X: 0, Y: 0", 
                        color = Color.Gray, 
                        fontSize = 9.sp, 
                        modifier = Modifier.align(Alignment.TopStart).padding(10.dp)
                    )
                }
            }

            // Size Slider
            Column {
                Text("Size ${ String.format("%.1fx", config.size) }", color = Color.Gray, fontSize = 10.sp)
                Slider(
                    value = config.size,
                    onValueChange = { newSize -> CrosshairState.update { it.copy(size = newSize) } },
                    valueRange = 0.5f..2.5f,
                    colors = SliderDefaults.colors(thumbColor = AccentOrange, activeTrackColor = AccentOrange)
                )
            }

            // Alpha Slider
            Column {
                Text("Alpha ${ (config.alpha * 100).toInt() }%", color = Color.Gray, fontSize = 10.sp)
                Slider(
                    value = config.alpha,
                    onValueChange = { newAlpha -> CrosshairState.update { it.copy(alpha = newAlpha) } },
                    colors = SliderDefaults.colors(thumbColor = AccentOrange, activeTrackColor = AccentOrange)
                )
            }

            // Rotation Slider
            Column {
                Text("Rotation ${ config.rotation.toInt() }°", color = Color.Gray, fontSize = 10.sp)
                Slider(
                    value = config.rotation,
                    onValueChange = { newRot -> CrosshairState.update { it.copy(rotation = newRot) } },
                    valueRange = 0f..360f,
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
                            .border(if (config.color == color) 2.dp else 0.dp, Color.White, RoundedCornerShape(12.dp))
                            .clickable { CrosshairState.update { it.copy(color = color) } }
                    )
                }
            }
        }
    }
}

@Composable
fun CrosshairStyleButton(styleId: String, isSelected: Boolean, color: Color, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .background(
                if (isSelected) AccentOrange.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.03f),
                RoundedCornerShape(12.dp)
            )
            .border(1.dp, if (isSelected) color else Color.Transparent, RoundedCornerShape(12.dp))
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size(20.dp)) {
            val drawColor = if (isSelected) color else Color.Gray
            val config = CrosshairState.config.value
            val strokeWidth = (config.thickness * 0.5f).dp.toPx() // Scaled down for preview
            val totalSize = size.width
            val center = totalSize / 2
            val halfLen = (totalSize / 2) * config.length
            
            when (styleId) {
                "cross" -> {
                    drawLine(drawColor, androidx.compose.ui.geometry.Offset(center, center - halfLen), androidx.compose.ui.geometry.Offset(center, center + halfLen), strokeWidth)
                    drawLine(drawColor, androidx.compose.ui.geometry.Offset(center - halfLen, center), androidx.compose.ui.geometry.Offset(center + halfLen, center), strokeWidth)
                }
                "dot" -> {
                    drawCircle(drawColor, radius = (totalSize / 6) * config.length)
                }
                "circle" -> {
                    drawCircle(drawColor, radius = halfLen, style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth))
                }
                "gap_cross" -> {
                    val gap = (totalSize / 4)
                    val lineLen = halfLen * 0.8f
                    drawLine(drawColor, androidx.compose.ui.geometry.Offset(center, center - gap - lineLen), androidx.compose.ui.geometry.Offset(center, center - gap), strokeWidth)
                    drawLine(drawColor, androidx.compose.ui.geometry.Offset(center, center + gap), androidx.compose.ui.geometry.Offset(center, center + gap + lineLen), strokeWidth)
                    drawLine(drawColor, androidx.compose.ui.geometry.Offset(center - gap - lineLen, center), androidx.compose.ui.geometry.Offset(center - gap, center), strokeWidth)
                    drawLine(drawColor, androidx.compose.ui.geometry.Offset(center + gap, center), androidx.compose.ui.geometry.Offset(center + gap + lineLen, center), strokeWidth)
                }
                "square" -> {
                    drawRect(
                        drawColor, 
                        topLeft = androidx.compose.ui.geometry.Offset(center - halfLen, center - halfLen),
                        size = androidx.compose.ui.geometry.Size(halfLen * 2, halfLen * 2),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth)
                    )
                }
                "t_shape" -> {
                    drawLine(drawColor, androidx.compose.ui.geometry.Offset(center, center), androidx.compose.ui.geometry.Offset(center, center + halfLen), strokeWidth)
                    drawLine(drawColor, androidx.compose.ui.geometry.Offset(center - halfLen, center), androidx.compose.ui.geometry.Offset(center + halfLen, center), strokeWidth)
                }
                "chevron" -> {
                    drawLine(drawColor, androidx.compose.ui.geometry.Offset(center - halfLen, center + halfLen), androidx.compose.ui.geometry.Offset(center, center), strokeWidth)
                    drawLine(drawColor, androidx.compose.ui.geometry.Offset(center + halfLen, center + halfLen), androidx.compose.ui.geometry.Offset(center, center), strokeWidth)
                }
                "x_shape" -> {
                    drawLine(drawColor, androidx.compose.ui.geometry.Offset(center - halfLen, center - halfLen), androidx.compose.ui.geometry.Offset(center + halfLen, center + halfLen), strokeWidth)
                    drawLine(drawColor, androidx.compose.ui.geometry.Offset(center + halfLen, center - halfLen), androidx.compose.ui.geometry.Offset(center - halfLen, center + halfLen), strokeWidth)
                }
            }
        }
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
