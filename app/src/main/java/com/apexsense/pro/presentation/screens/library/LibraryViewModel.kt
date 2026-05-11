package com.apexsense.pro.presentation.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apexsense.pro.data.repository.AppRepository
import com.apexsense.pro.domain.model.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LibraryState(
    val games: List<Game> = emptyList(),
    val isLoading: Boolean = false
)

class LibraryViewModel : ViewModel() {
    private val repository = AppRepository()
    
    private val _state = MutableStateFlow(LibraryState())
    val state: StateFlow<LibraryState> = _state

    init {
        loadGames()
    }

    fun loadGames() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            _state.value = _state.value.copy(
                games = repository.getGames(),
                isLoading = false
            )
        }
    }

    fun addGame(name: String, iconUrl: String) {
        viewModelScope.launch {
            repository.addGame(Game(name = name, icon_url = iconUrl))
            loadGames()
        }
    }
}
