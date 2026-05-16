package com.apexsense.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import com.apexsense.presentation.navigation.Screen
import com.apexsense.presentation.theme.*
import com.apexsense.presentation.components.CopyrightFooter
import com.apexsense.presentation.components.PageHeader

@OptIn(ExperimentalMaterial3Api::class)
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
            PageHeader(title = "Profil")
            
            val context = LocalContext.current
            val deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}"
            val androidVer = "${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
            val abi = Build.SUPPORTED_ABIS.firstOrNull() ?: "unknown"
            val totalRam = getDeviceInfoRam(context)

            var showLanguageSheet by remember { mutableStateOf(false) }
            var searchQuery by remember { mutableStateOf("") }

            // Language Bottom Sheet
            if (showLanguageSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showLanguageSheet = false },
                    containerColor = Color(0xFF1A1614),
                    dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray.copy(alpha = 0.3f)) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .padding(bottom = 32.dp)
                    ) {
                        Text(
                            "Bahasa",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Search Box
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Cari bahasa", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Gray,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        
                        Text(
                            "Viralkan Game Corner ini dinegaramu jika tidak ada bahasamu disini",
                            color = AccentOrange,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                        )

                        val languages = mapOf(
                            "Default Sistem" to "",
                            "English" to "en",
                            "Indonesia" to "id"
                        )
                        
                        // Get current locale
                        val currentLocale = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales().get(0)?.language ?: ""
                        
                        languages.keys.filter { lang -> lang.lowercase().contains(searchQuery.lowercase()) }.forEach { lang ->
                            val localeCode = languages[lang] ?: ""
                            val isSelected = if (lang == "Default Sistem") currentLocale == "" else currentLocale == localeCode

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { 
                                        val appLocale: androidx.core.os.LocaleListCompat = if (localeCode.isEmpty()) {
                                            androidx.core.os.LocaleListCompat.getEmptyLocaleList()
                                        } else {
                                            androidx.core.os.LocaleListCompat.forLanguageTags(localeCode)
                                        }
                                        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale)
                                        showLanguageSheet = false 
                                    }
                                    .padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(lang, color = Color.White, fontSize = 16.sp)
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { 
                                        val appLocale: androidx.core.os.LocaleListCompat = if (localeCode.isEmpty()) {
                                            androidx.core.os.LocaleListCompat.getEmptyLocaleList()
                                        } else {
                                            androidx.core.os.LocaleListCompat.forLanguageTags(localeCode)
                                        }
                                        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale)
                                        showLanguageSheet = false 
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = AccentOrange, unselectedColor = Color.Gray)
                                )
                            }
                        }
                    }
                }
            }

            // Info Sistem Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Info Sistem",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Version Pill
                    Surface(
                        color = Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            "V1.2.4-12400",
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // Gear Icon
                    IconButton(
                        onClick = { showLanguageSheet = true },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.05f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

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
            
            Spacer(modifier = Modifier.weight(1f))
            CopyrightFooter()
            Spacer(modifier = Modifier.height(20.dp))
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
