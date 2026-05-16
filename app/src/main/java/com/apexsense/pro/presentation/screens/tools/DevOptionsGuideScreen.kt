package com.apexsense.pro.presentation.screens.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.apexsense.pro.presentation.theme.AccentOrange
import com.apexsense.pro.presentation.theme.DarkBackground
import com.apexsense.pro.presentation.theme.SurfaceGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevOptionsGuideScreen(navController: NavController) {
    val steps = listOf(
        GuideStep("Buka Pengaturan", "Buka aplikasi Pengaturan di perangkatmu.", Icons.Default.Settings),
        GuideStep("Tentang Ponsel", "Cari dan pilih menu 'Tentang Ponsel' atau 'Informasi Perangkat'.", Icons.Default.Settings),
        GuideStep("Ketuk Nomor Bentukan", "Cari 'Nomor Bentukan' (Build Number) dan ketuk sebanyak 7 kali berturut-turut.", Icons.Default.CheckCircle),
        GuideStep("Selesai!", "Sekarang Opsi Pengembang sudah aktif! Kembali ke aplikasi ini.", Icons.Default.CheckCircle)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aktivasi Opsi Pengembang", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Ikuti langkah-langkah di bawah untuk membuka fitur tingkat lanjut perangkatmu.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            itemsIndexed(steps) { index, step ->
                StepCard(index + 1, step)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("SAYA MENGERTI", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

data class GuideStep(val title: String, val desc: String, val icon: ImageVector)

@Composable
fun StepCard(number: Int, step: GuideStep) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(AccentOrange.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("$number", color = AccentOrange, fontWeight = FontWeight.Black, fontSize = 18.sp)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(step.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(step.desc, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}
