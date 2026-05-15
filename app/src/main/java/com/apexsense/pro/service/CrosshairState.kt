package com.apexsense.pro.service

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.flow.MutableStateFlow

data class CrosshairConfig(
    val style: String = "cross",
    val color: Color = Color.Red,
    val size: Float = 1.0f,
    val thickness: Float = 2.0f,
    val length: Float = 1.0f,
    val alpha: Float = 1.0f,
    val rotation: Float = 0f
)

object CrosshairState {
    val config = MutableStateFlow(CrosshairConfig())
    
    val availableStyles = listOf(
        "cross", "dot", "circle", "gap_cross", "square", "t_shape", "chevron", "x_shape"
    )
    
    fun update(update: (CrosshairConfig) -> CrosshairConfig) {
        config.value = update(config.value)
    }
}
