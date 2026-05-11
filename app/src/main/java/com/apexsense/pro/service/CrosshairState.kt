package com.apexsense.pro.service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

object CrosshairState {
    var style by mutableStateOf<ImageVector>(Icons.Filled.Add)
    var color by mutableStateOf(Color.Red)
    var size by mutableFloatStateOf(1.0f)
    var alpha by mutableFloatStateOf(1.0f)
}
