package com.example.premiumapp.feature.selection

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

data class SelectionUiState(
    val allMedia: List<MediaItem> = emptyList(),
    val selectedIds: Set<Long> = emptySet(),
    val availableCollections: List<CollectionItem> = emptyList(),
    val isLoading: Boolean = true
)

class SelectionViewModel(
    private val mediaRepository: MediaRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SelectionUiState())
    val uiState: StateFlow<SelectionUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            launch {
                mediaRepository.getAllMedia().collect { items ->
                    _uiState.value = _uiState.value.copy(allMedia = items, isLoading = false)
                }
            }
            launch {
                collectionRepository.getAllCollections().collect { cols ->
                    _uiState.value = _uiState.value.copy(availableCollections = cols)
                }
            }
        }
    }

    fun toggleItem(id: Long) {
        val current = _uiState.value.selectedIds.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        _uiState.value = _uiState.value.copy(selectedIds = current)
    }

    fun selectAll() {
        val allIds = _uiState.value.allMedia.map { it.id }.toSet()
        _uiState.value = _uiState.value.copy(selectedIds = allIds)
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedIds = emptySet())
    }

    fun batchFavorite() {
        val selected = _uiState.value.selectedIds
        viewModelScope.launch {
            for (id in selected) {
                mediaRepository.toggleFavorite(id, true)
            }
            clearSelection()
        }
    }

    fun batchDelete(onCompleted: () -> Unit) {
        val selected = _uiState.value.selectedIds.toList()
        viewModelScope.launch {
            mediaRepository.deleteMediaBatch(selected)
            clearSelection()
            onCompleted()
        }
    }

    fun batchAddToCollection(collectionId: Long) {
        val selected = _uiState.value.selectedIds.toList()
        viewModelScope.launch {
            collectionRepository.addMediaBatchToCollection(collectionId, selected)
            clearSelection()
        }
    }
}
