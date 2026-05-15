package com.apexsense.pro.presentation.screens.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apexsense.pro.data.repository.AppRepository
import com.apexsense.pro.domain.model.Device
import com.apexsense.pro.domain.model.Feedback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ResultState(
    val device: Device? = null,
    val isLoading: Boolean = false,
    val isFeedbackSent: Boolean = false,
    val error: String? = null
)

class ResultViewModel : ViewModel() {
    private val repository = AppRepository()
    
    private val _state = MutableStateFlow(ResultState())
    val state: StateFlow<ResultState> = _state

    fun loadResult(width: Int, height: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = repository.getRecommendedSensitivity(width, height)
            
            val deviceData = if (result != null) {
                result
            } else {
                // Smart Fallback Logic
                val resolutionFactor = (width.toDouble() * height.toDouble()) / 1_000_000.0
                val baseSens = (120.0 + (resolutionFactor * 8.0)).coerceIn(0.0, 200.0)
                
                Device(
                    screen_width = width,
                    screen_height = height,
                    recommended_dpi = if (width > 1000) 480 else 400,
                    gen_sens = Math.round(baseSens).toDouble(),
                    red_dot_sens = Math.round(baseSens * 0.95).toDouble().coerceAtMost(200.0),
                    scope_2x_sens = Math.round(baseSens * 0.88).toDouble().coerceAtMost(200.0),
                    scope_4x_sens = Math.round(baseSens * 0.78).toDouble().coerceAtMost(200.0),
                    sniper_sens = Math.round(baseSens * 0.55).toDouble().coerceAtMost(200.0),
                    free_look_sens = Math.round(baseSens).toDouble()
                )
            }
            
            _state.value = _state.value.copy(
                device = deviceData,
                isLoading = false
            )
        }
    }

    fun submitFeedback(deviceId: Int, rating: String, sensation: String) {
        viewModelScope.launch {
            val success = repository.sendFeedback(
                Feedback(device_id = deviceId, rating = rating, sensation = sensation)
            )
            if (success) _state.value = _state.value.copy(isFeedbackSent = true)
        }
    }
}
