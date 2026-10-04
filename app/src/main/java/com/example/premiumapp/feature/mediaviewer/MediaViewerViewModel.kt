package com.example.premiumapp.feature.mediaviewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MediaViewerViewModel(
    private val mediaId: Long,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _mediaItem = MutableStateFlow<MediaItem?>(null)
    val mediaItem: StateFlow<MediaItem?> = _mediaItem.asStateFlow()

    init {
        loadMedia()
    }

    private fun loadMedia() {
        viewModelScope.launch {
            mediaRepository.getMediaById(mediaId).collect { item ->
                _mediaItem.value = item
            }
        }
    }

    fun toggleFavorite() {
        val item = _mediaItem.value ?: return
        viewModelScope.launch {
            mediaRepository.toggleFavorite(item.id, !item.isFavorite)
        }
    }
}
