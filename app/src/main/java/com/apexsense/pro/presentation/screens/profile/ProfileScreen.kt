package com.apexsense.pro.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import com.apexsense.pro.presentation.navigation.Screen
import com.apexsense.pro.presentation.theme.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Adjust

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Info Sistem", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "V1.2.3-12300",
                            color = Color.Gray,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Filled.Settings, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            val context = LocalContext.current
            val deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}"
            val androidVer = "${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
            val abi = Build.SUPPORTED_ABIS.firstOrNull() ?: "unknown"
            val totalRam = getDeviceInfoRam(context)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SystemInfoCard(
                    modifier = Modifier.weight(1f),
                    label = "Model Perangkat",
                    value = deviceModel,
                    icon = Icons.Filled.Smartphone
                )
                SystemInfoCard(
                    modifier = Modifier.weight(1f),
                    label = "Total RAM",
                    value = totalRam,
                    icon = Icons.Filled.Bolt
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SystemInfoCard(
                    modifier = Modifier.weight(1f),
                    label = "Versi Android",
                    value = androidVer,
                    icon = Icons.Filled.Android
                )
                SystemInfoCard(
                    modifier = Modifier.weight(1f),
                    label = "ABI yang Didukung",
                    value = abi,
                    icon = Icons.Filled.Adjust
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SystemInfoCard(modifier: Modifier, label: String, value: String, icon: ImageVector) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.heightIn(min = 100.dp).border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(label, color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value, 
                color = Color.White, 
                fontSize = 11.sp, 
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )
        }
    }
}

fun getDeviceInfoRam(context: Context): String {
    val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memInfo = ActivityManager.MemoryInfo()
    actManager.getMemoryInfo(memInfo)
    val totalRamGb = memInfo.totalMem.toDouble() / (1024 * 1024 * 1024)
    return String.format("%.1f GB", totalRamGb)
}

@Composable
fun ProfileMenuItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceGray, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(AccentOrange.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}
