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
            _state.value = _state.value.copy(
                device = result ?: Device(screen_width = width, screen_height = height), // Baseline if null
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
