package com.apexsense.pro.service

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.flow.MutableStateFlow

data class CrosshairConfig(
    val style: ImageVector = Icons.Filled.Add,
    val color: Color = Color.Red,
    val size: Float = 1.0f,
    val alpha: Float = 1.0f,
    val rotation: Float = 0f
)

object CrosshairState {
    val config = MutableStateFlow(CrosshairConfig())
    
    fun update(update: (CrosshairConfig) -> CrosshairConfig) {
        config.value = update(config.value)
    }
}
