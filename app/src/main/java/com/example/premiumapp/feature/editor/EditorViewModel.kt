package com.example.premiumapp.feature.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.core.common.AppResult
import com.example.premiumapp.core.media.ImageEditParams
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.repository.MediaRepository
import com.example.premiumapp.domain.usecase.EditMediaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EditorUiState(
    val mediaItem: MediaItem? = null,
    val imageParams: ImageEditParams = ImageEditParams(),
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = 0L,
    val isProcessing: Boolean = false,
    val savedItem: MediaItem? = null,
    val errorMessage: String? = null
)

class EditorViewModel(
    private val mediaId: Long,
    private val mediaRepository: MediaRepository,
    private val editMediaUseCase: EditMediaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init {
        loadMedia()
    }

    private fun loadMedia() {
        viewModelScope.launch {
            mediaRepository.getMediaById(mediaId).collect { item ->
                if (item != null) {
                    val duration = item.durationMs ?: 0L
                    _uiState.value = _uiState.value.copy(
                        mediaItem = item,
                        trimStartMs = 0L,
                        trimEndMs = duration
                    )
                }
            }
        }
    }

    fun rotate() {
        val current = _uiState.value.imageParams
        val newRotation = (current.rotationDegrees + 90f) % 360f
        _uiState.value = _uiState.value.copy(
            imageParams = current.copy(rotationDegrees = newRotation)
        )
    }

    fun flipHorizontal() {
        val current = _uiState.value.imageParams
        _uiState.value = _uiState.value.copy(
            imageParams = current.copy(flipHorizontal = !current.flipHorizontal)
        )
    }

    fun flipVertical() {
        val current = _uiState.value.imageParams
        _uiState.value = _uiState.value.copy(
            imageParams = current.copy(flipVertical = !current.flipVertical)
        )
    }

    fun setBrightness(brightness: Float) {
        _uiState.value = _uiState.value.copy(
            imageParams = _uiState.value.imageParams.copy(brightness = brightness)
        )
    }

    fun setContrast(contrast: Float) {
        _uiState.value = _uiState.value.copy(
            imageParams = _uiState.value.imageParams.copy(contrast = contrast)
        )
    }

    fun setSaturation(saturation: Float) {
        _uiState.value = _uiState.value.copy(
            imageParams = _uiState.value.imageParams.copy(saturation = saturation)
        )
    }

    fun setTrimRange(startMs: Long, endMs: Long) {
        _uiState.value = _uiState.value.copy(
            trimStartMs = startMs,
            trimEndMs = endMs
        )
    }

    fun resetEdits() {
        val duration = _uiState.value.mediaItem?.durationMs ?: 0L
        _uiState.value = _uiState.value.copy(
            imageParams = ImageEditParams(),
            trimStartMs = 0L,
            trimEndMs = duration,
            errorMessage = null
        )
    }

    fun saveCopy(onSaved: (Long) -> Unit) {
        val item = _uiState.value.mediaItem ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, errorMessage = null)

            val result = if (item.isVideo) {
                editMediaUseCase.trimVideo(item, _uiState.value.trimStartMs, _uiState.value.trimEndMs)
            } else {
                editMediaUseCase.editImage(item, _uiState.value.imageParams)
            }

            when (result) {
                is AppResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        savedItem = result.data
                    )
                    onSaved(result.data.id)
                }
                is AppResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        errorMessage = result.error.message
                    )
                }
            }
        }
    }
}
