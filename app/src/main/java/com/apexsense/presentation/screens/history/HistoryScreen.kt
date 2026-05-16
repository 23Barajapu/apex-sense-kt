package com.apexsense.presentation.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.apexsense.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, viewModel: HistoryViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("PERF LOGS", fontSize = 16.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                        Text("HARDWARE ANALYTICS", fontSize = 10.sp, color = AccentOrange)
                    }
                },
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
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentOrange)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                
                items(state.records) { record ->
                    ModernHistoryItem(record)
                }
            }
        }
    }
}

@Composable
fun ModernHistoryItem(record: com.apexsense.domain.model.HardwareHistory) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    record.device_model.uppercase(), 
                    color = Color.White, 
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
                Text(
                    record.created_at?.take(16)?.replace("T", " ") ?: "RECENT SCAN", 
                    color = Color.Gray, 
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                HistoryMetricPro("CPU", "${record.cpu_usage}%")
                HistoryMetricPro("TEMP", "${record.temp.toInt()}°C")
            }
        }
    }
}

@Composable
fun HistoryMetricPro(label: String, value: String) {
    Column(horizontalAlignment = Alignment.End) {
        Text(label, color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(value, color = AccentOrange, fontWeight = FontWeight.Black, fontSize = 16.sp)
    }
}
