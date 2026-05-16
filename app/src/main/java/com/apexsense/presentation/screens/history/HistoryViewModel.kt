package com.apexsense.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apexsense.data.repository.AppRepository
import com.apexsense.domain.model.HardwareHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HistoryState(
    val records: List<HardwareHistory> = emptyList(),
    val isLoading: Boolean = false
)

class HistoryViewModel : ViewModel() {
    private val repository = AppRepository()
    
    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            _state.value = _state.value.copy(
                records = repository.getHardwareHistory(),
                isLoading = false
            )
        }
    }
}
