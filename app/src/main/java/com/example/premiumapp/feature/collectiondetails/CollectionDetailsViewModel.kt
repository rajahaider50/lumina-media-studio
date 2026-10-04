package com.example.premiumapp.feature.collectiondetails

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

data class CollectionDetailsUiState(
    val collection: CollectionItem? = null,
    val mediaItems: List<MediaItem> = emptyList(),
    val allLibraryMedia: List<MediaItem> = emptyList(),
    val isLoading: Boolean = true
)

class CollectionDetailsViewModel(
    private val collectionId: Long,
    private val collectionRepository: CollectionRepository,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionDetailsUiState())
    val uiState: StateFlow<CollectionDetailsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            launch {
                collectionRepository.getCollectionById(collectionId).collect { col ->
                    _uiState.value = _uiState.value.copy(collection = col)
                }
            }

            launch {
                collectionRepository.getMediaForCollection(collectionId).collect { items ->
                    _uiState.value = _uiState.value.copy(mediaItems = items, isLoading = false)
                }
            }

            launch {
                mediaRepository.getAllMedia().collect { all ->
                    _uiState.value = _uiState.value.copy(allLibraryMedia = all)
                }
            }
        }
    }

    fun renameCollection(newName: String) {
        viewModelScope.launch {
            val current = _uiState.value.collection ?: return@launch
            collectionRepository.updateCollection(current.copy(name = newName))
        }
    }

    fun deleteCollection(onDeleted: () -> Unit) {
        viewModelScope.launch {
            collectionRepository.deleteCollection(collectionId)
            onDeleted()
        }
    }

    fun addMediaToCollection(mediaId: Long) {
        viewModelScope.launch {
            collectionRepository.addMediaToCollection(collectionId, mediaId)
        }
    }

    fun removeMediaFromCollection(mediaId: Long) {
        viewModelScope.launch {
            collectionRepository.removeMediaFromCollection(collectionId, mediaId)
        }
    }
}
