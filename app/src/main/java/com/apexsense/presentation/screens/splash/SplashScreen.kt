package com.apexsense.presentation.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.apexsense.presentation.navigation.Screen
import com.apexsense.presentation.theme.AccentOrange
import com.apexsense.presentation.theme.DarkBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavController) {
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.8f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffset = remember { Animatable(30f) }

    LaunchedEffect(Unit) {
        launch {
            logoAlpha.animateTo(1f, animationSpec = tween(1000, easing = FastOutSlowInEasing))
        }
        launch {
            logoScale.animateTo(1f, animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            ))
        }
        delay(600)
        launch {
            textAlpha.animateTo(1f, animationSpec = tween(1200))
        }
        launch {
            textOffset.animateTo(0f, animationSpec = tween(1000, easing = FastOutSlowInEasing))
        }
        
        delay(2500)
        navController.navigate(Screen.MainContainer.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val logoId = remember(context) {
                context.resources.getIdentifier("app_logo", "drawable", context.packageName)
            }
            Image(
                painter = painterResource(id = if (logoId != 0) logoId else android.R.drawable.ic_menu_gallery),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer(
                        alpha = logoAlpha.value,
                        scaleX = logoScale.value,
                        scaleY = logoScale.value
                    )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "APEXSENSE",
                color = AccentOrange,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp,
                modifier = Modifier
                    .graphicsLayer(
                        alpha = textAlpha.value,
                        translationY = textOffset.value
                    )
            )
        }
    }
}
