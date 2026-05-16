package com.apexsense.presentation.screens.tools

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
import androidx.compose.ui.res.stringResource
import com.apexsense.R
import com.apexsense.presentation.theme.AccentOrange
import com.apexsense.presentation.theme.DarkBackground
import com.apexsense.presentation.theme.SurfaceGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevOptionsGuideScreen(navController: NavController) {
    val steps = listOf(
        GuideStep(stringResource(id = R.string.step_settings), stringResource(id = R.string.step_settings_desc), Icons.Default.Settings),
        GuideStep(stringResource(id = R.string.step_about), stringResource(id = R.string.step_about_desc), Icons.Default.Settings),
        GuideStep(stringResource(id = R.string.step_build), stringResource(id = R.string.step_build_desc), Icons.Default.CheckCircle),
        GuideStep(stringResource(id = R.string.step_done), stringResource(id = R.string.step_done_desc), Icons.Default.CheckCircle)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.dev_options_title), fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
                    stringResource(id = R.string.dev_options_desc),
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
                    Text(stringResource(id = R.string.i_understand), fontWeight = FontWeight.Bold)
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
