package com.apexsense.pro.presentation.screens.result

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.apexsense.pro.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navController: NavController,
    width: Int,
    height: Int,
    viewModel: ResultViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadResult(width, height)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("SYSTEM SCAN", fontSize = 16.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                        Text("SENSITIVITY OPTIMIZATION", fontSize = 10.sp, color = AccentOrange)
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
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentOrange)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Device Info Banner
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AccentOrange.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(4.dp, 24.dp).background(AccentOrange))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "MATCHING CONFIG FOR ${width}x${height}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text("OPTIMIZED VALUES", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    SensitivityGridPro(state.device)
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    FeedbackSectionPro { rating, sensation ->
                        state.device?.id?.let { viewModel.submitFeedback(it, rating, sensation) }
                    }
                }
            }
        }
    }
}

@Composable
fun SensitivityGridPro(device: com.apexsense.pro.domain.model.Device?) {
    val items = listOf(
        "DPI" to (device?.recommended_dpi ?: 440).toString(),
        "GENERAL" to (device?.gen_sens?.toInt() ?: 95).toString(),
        "RED DOT" to (device?.red_dot_sens?.toInt() ?: 90).toString(),
        "2X SCOPE" to (device?.scope_2x_sens?.toInt() ?: 85).toString(),
        "4X SCOPE" to (device?.scope_4x_sens?.toInt() ?: 80).toString(),
        "SNIPER" to (device?.sniper_sens?.toInt() ?: 50).toString()
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.height(360.dp)
    ) {
        items(items) { (label, value) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceGray),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(label, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp)
                    Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(AccentOrange.copy(alpha = 0.3f)))
                }
            }
        }
    }
}

@Composable
fun FeedbackSectionPro(onSubmit: (String, String) -> Unit) {
    var sensation by remember { mutableStateOf("Pas") }
    
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("CALIBRATION FEEDBACK", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Licin", "Pas", "Kesat").forEach { option ->
                    val isSelected = sensation == option
                    Button(
                        onClick = { sensation = option },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) AccentOrange else CardGray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(option, fontSize = 12.sp, color = if (isSelected) Color.White else Color.Gray)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = { onSubmit("Upvote", sensation) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentOrange)
                ) {
                    Icon(Icons.Filled.ThumbUp, contentDescription = null, tint = AccentOrange)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PERFECT", color = AccentOrange)
                }
                OutlinedButton(
                    onClick = { onSubmit("Downvote", sensation) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
                ) {
                    Icon(Icons.Filled.ThumbDown, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ADJUST", color = Color.Gray)
                }
            }
        }
    }
}
