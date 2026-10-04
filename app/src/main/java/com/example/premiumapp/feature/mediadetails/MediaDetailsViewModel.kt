package com.example.premiumapp.feature.mediadetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.domain.model.CollectionItem
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.repository.CollectionRepository
import com.example.premiumapp.domain.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MediaDetailsUiState(
    val mediaItem: MediaItem? = null,
    val collections: List<CollectionItem> = emptyList(),
    val availableCollections: List<CollectionItem> = emptyList(),
    val isLoading: Boolean = true
)

class MediaDetailsViewModel(
    private val mediaId: Long,
    private val mediaRepository: MediaRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MediaDetailsUiState())
    val uiState: StateFlow<MediaDetailsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            launch {
                mediaRepository.getMediaById(mediaId).collect { item ->
                    _uiState.value = _uiState.value.copy(mediaItem = item, isLoading = false)
                }
            }

            launch {
                collectionRepository.getAllCollections().collect { allCols ->
                    _uiState.value = _uiState.value.copy(availableCollections = allCols)
                }
            }
        }
    }

    fun toggleFavorite() {
        val item = _uiState.value.mediaItem ?: return
        viewModelScope.launch {
            mediaRepository.toggleFavorite(mediaId, !item.isFavorite)
        }
    }

    fun deleteMedia(onDeleted: () -> Unit) {
        viewModelScope.launch {
            mediaRepository.deleteMedia(mediaId)
            onDeleted()
        }
    }

    fun addToCollection(collectionId: Long) {
        viewModelScope.launch {
            collectionRepository.addMediaToCollection(collectionId, mediaId)
        }
    }
}
