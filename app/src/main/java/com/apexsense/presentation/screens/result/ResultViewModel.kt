package com.apexsense.presentation.screens.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apexsense.data.repository.AppRepository
import com.apexsense.domain.model.Device
import com.apexsense.domain.model.Feedback
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
                // Pro-Level High Response Logic
                val resolutionFactor = (width.toDouble() * height.toDouble()) / 1_000_000.0
                val baseSens = (175.0 + (resolutionFactor * 1.5)).coerceIn(160.0, 190.0)
                
                Device(
                    screen_width = width,
                    screen_height = height,
                    recommended_dpi = if (width > 1000) 480 else 440,
                    gen_sens = Math.round(baseSens).toDouble(),
                    red_dot_sens = Math.round(baseSens * 1.11).toDouble().coerceAtMost(200.0),
                    scope_2x_sens = Math.round(baseSens * 1.08).toDouble().coerceAtMost(200.0),
                    scope_4x_sens = Math.round(baseSens * 1.09).toDouble().coerceAtMost(200.0),
                    sniper_sens = Math.round(baseSens * 0.68).toDouble().coerceAtMost(200.0),
                    free_look_sens = Math.round(baseSens * 0.77).toDouble().coerceAtMost(200.0)
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
