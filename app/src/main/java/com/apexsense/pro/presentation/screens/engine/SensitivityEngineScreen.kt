package com.apexsense.pro.presentation.screens.engine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.apexsense.pro.presentation.navigation.Screen
import com.apexsense.pro.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensitivityEngineScreen(navController: NavController) {
    var width by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var dpi by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SENSITIVITY ENGINE", fontWeight = FontWeight.Black, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                "Masukan spesifikasi layar perangkat Anda untuk mendapatkan kalkulasi sensitivitas terbaik.",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceGray),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    EngineTextField(
                        value = width,
                        onValueChange = { width = it },
                        label = "Lebar Layar (Pixels)",
                        placeholder = "Contoh: 1080"
                    )
                    
                    EngineTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = "Panjang Layar (Pixels)",
                        placeholder = "Contoh: 2400"
                    )
                    
                    EngineTextField(
                        value = dpi,
                        onValueChange = { dpi = it },
                        label = "DPI Normal (Opsional)",
                        placeholder = "Kosongkan jika tidak tahu"
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (width.isNotEmpty() && height.isNotEmpty()) {
                        navController.navigate(Screen.SensitivityResult.createRoute(width.toInt(), height.toInt()))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.FlashOn, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("GENERATE SENSITIVITY", fontWeight = FontWeight.Black)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EngineTextField(value: String, onValueChange: (String) -> Unit, label: String, placeholder: String) {
    Column {
        Text(label, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 14.sp, color = Color.Gray.copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentOrange,
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}
