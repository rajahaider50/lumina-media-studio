package com.example.premiumapp.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favorites: List<MediaItem> = emptyList(),
    val isListView: Boolean = false,
    val isLoading: Boolean = true
)

class FavoritesViewModel(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            mediaRepository.getFavorites().collect { items ->
                _uiState.value = _uiState.value.copy(
                    favorites = items,
                    isLoading = false
                )
            }
        }
    }

    fun toggleLayout() {
        _uiState.value = _uiState.value.copy(isListView = !_uiState.value.isListView)
    }

    fun removeFavorite(id: Long) {
        viewModelScope.launch {
            mediaRepository.toggleFavorite(id, false)
        }
    }
}
