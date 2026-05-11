package com.apexsense.pro.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

val DarkBackground = Color(0xFF0F0F0F)
val AccentOrange = Color(0xFFFF8C00)
val SurfaceGray = Color(0xFF1E1E1E)
val CardGray = Color(0xFF262626)

val ApexColorScheme = darkColorScheme(
    primary = AccentOrange,
    background = DarkBackground,
    surface = SurfaceGray,
    onBackground = Color.White,
    onSurface = Color.White
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default, // Will use Outfit if available via Google Fonts, else default
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        color = Color.White
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        color = Color.White
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = Color.LightGray
    )
)

@Composable
fun ApexSenseTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ApexColorScheme,
        typography = Typography,
        content = content
    )
}
