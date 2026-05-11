package com.apexsense.pro.presentation.screens.result

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.apexsense.pro.presentation.theme.AccentOrange
import com.apexsense.pro.presentation.theme.CardGray
import com.apexsense.pro.presentation.theme.DarkBackground
import com.apexsense.pro.presentation.theme.SurfaceGray

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
                title = { Text("Recommendations") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentOrange)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                if (state.device?.id == null) {
                    Text(
                        "Default Baseline Applied",
                        color = Color.Yellow,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                SensitivityGrid(state.device)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                FeedbackSection { rating, sensation ->
                    state.device?.id?.let { viewModel.submitFeedback(it, rating, sensation) }
                }
            }
        }
    }
}

@Composable
fun SensitivityGrid(device: com.apexsense.pro.domain.model.Device?) {
    val items = listOf(
        "DPI" to (device?.recommended_dpi ?: 440).toString(),
        "General" to (device?.gen_sens ?: 95.0).toString(),
        "Red Dot" to (device?.red_dot_sens ?: 90.0).toString(),
        "2x Scope" to (device?.scope_2x_sens ?: 85.0).toString(),
        "4x Scope" to (device?.scope_4x_sens ?: 80.0).toString(),
        "Sniper" to (device?.sniper_sens ?: 50.0).toString()
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.height(300.dp)
    ) {
        items(items) { (label, value) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceGray),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(label, color = Color.Gray, fontSize = 12.sp)
                    Text(value, color = AccentOrange, fontWeight = FontWeight.Black, fontSize = 24.sp)
                }
            }
        }
    }
}

@Composable
fun FeedbackSection(onSubmit: (String, String) -> Unit) {
    var sensation by remember { mutableStateOf("Pas") }
    
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Is it perfect?", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                IconButton(onClick = { onSubmit("Upvote", sensation) }) {
                    Icon(Icons.Filled.ThumbUp, contentDescription = null, tint = AccentOrange)
                }
                IconButton(onClick = { onSubmit("Downvote", sensation) }) {
                    Icon(Icons.Filled.ThumbDown, contentDescription = null, tint = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Licin", "Pas", "Kesat").forEach { option ->
                    FilterChip(
                        selected = sensation == option,
                        onClick = { sensation = option },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentOrange,
                            labelColor = Color.Gray,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}
