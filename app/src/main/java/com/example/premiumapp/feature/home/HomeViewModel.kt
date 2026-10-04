package com.example.premiumapp.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.domain.model.CollectionItem
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.model.StorageStats
import com.example.premiumapp.domain.repository.CollectionRepository
import com.example.premiumapp.domain.repository.MediaRepository
import com.example.premiumapp.domain.repository.PreferencesRepository
import com.example.premiumapp.domain.usecase.GetStorageInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "Creator",
    val recentMedia: List<MediaItem> = emptyList(),
    val favoriteMedia: List<MediaItem> = emptyList(),
    val collections: List<CollectionItem> = emptyList(),
    val storageStats: StorageStats = StorageStats(),
    val totalMediaCount: Int = 0,
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val mediaRepository: MediaRepository,
    private val collectionRepository: CollectionRepository,
    private val preferencesRepository: PreferencesRepository,
    private val getStorageInfoUseCase: GetStorageInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            launch {
                preferencesRepository.getUserName().collect { name ->
                    _uiState.value = _uiState.value.copy(userName = name)
                }
            }

            launch {
                mediaRepository.getRecentMedia(8).collect { mediaList ->
                    _uiState.value = _uiState.value.copy(recentMedia = mediaList)
                }
            }

            launch {
                mediaRepository.getFavorites().collect { favorites ->
                    _uiState.value = _uiState.value.copy(favoriteMedia = favorites.take(8))
                }
            }

            launch {
                collectionRepository.getAllCollections().collect { cols ->
                    _uiState.value = _uiState.value.copy(collections = cols.take(6))
                }
            }

            launch {
                mediaRepository.getMediaCount().collect { count ->
                    _uiState.value = _uiState.value.copy(totalMediaCount = count)
                }
            }

            refreshStorage()
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun refreshStorage() {
        viewModelScope.launch {
            val stats = getStorageInfoUseCase()
            _uiState.value = _uiState.value.copy(storageStats = stats)
        }
    }

    fun toggleFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            mediaRepository.toggleFavorite(id, isFavorite)
        }
    }
}
