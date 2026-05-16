package com.apexsense.presentation.screens.engine

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import com.apexsense.presentation.navigation.Screen
import androidx.compose.ui.res.stringResource
import com.apexsense.R
import com.apexsense.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensitivityEngineScreen(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    
    var width by remember { mutableStateOf(displayMetrics.widthPixels.toString()) }
    var height by remember { mutableStateOf(displayMetrics.heightPixels.toString()) }
    var dpi by remember { mutableStateOf(displayMetrics.densityDpi.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.sensitivity_engine_title), fontWeight = FontWeight.Black, fontSize = 16.sp) },
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
                stringResource(id = R.string.sensitivity_engine_info),
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
                        label = stringResource(id = R.string.screen_width_label),
                        placeholder = stringResource(id = R.string.example_width)
                    )
                    
                    EngineTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = stringResource(id = R.string.screen_height_label),
                        placeholder = stringResource(id = R.string.example_height)
                    )
                    
                    EngineTextField(
                        value = dpi,
                        onValueChange = { dpi = it },
                        label = stringResource(id = R.string.normal_dpi_label),
                        placeholder = stringResource(id = R.string.dpi_placeholder)
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
                Text(stringResource(id = R.string.generate_sensitivity), fontWeight = FontWeight.Black)
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentOrange,
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}
