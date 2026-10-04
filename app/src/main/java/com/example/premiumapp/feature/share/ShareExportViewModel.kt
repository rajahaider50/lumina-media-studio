package com.example.premiumapp.feature.share

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.core.common.AppResult
import com.example.premiumapp.core.media.MediaExporter
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.repository.MediaRepository
import com.example.premiumapp.domain.usecase.ExportMediaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ShareExportUiState(
    val mediaItem: MediaItem? = null,
    val isExporting: Boolean = false,
    val exportSuccessMessage: String? = null,
    val errorMessage: String? = null
)

class ShareExportViewModel(
    private val mediaId: Long,
    private val mediaRepository: MediaRepository,
    private val exportMediaUseCase: ExportMediaUseCase,
    private val mediaExporter: MediaExporter
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShareExportUiState())
    val uiState: StateFlow<ShareExportUiState> = _uiState.asStateFlow()

    init {
        loadMedia()
    }

    private fun loadMedia() {
        viewModelScope.launch {
            mediaRepository.getMediaById(mediaId).collect { item ->
                _uiState.value = _uiState.value.copy(mediaItem = item)
            }
        }
    }

    fun exportToGallery() {
        val item = _uiState.value.mediaItem ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, exportSuccessMessage = null, errorMessage = null)
            when (val result = exportMediaUseCase.toGallery(item)) {
                is AppResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isExporting = false,
                        exportSuccessMessage = "Saved to Device Gallery successfully!"
                    )
                }
                is AppResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isExporting = false,
                        errorMessage = result.error.message
                    )
                }
            }
        }
    }

    fun exportToDestinationUri(destinationUri: Uri) {
        val item = _uiState.value.mediaItem ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, exportSuccessMessage = null, errorMessage = null)
            when (val result = exportMediaUseCase.toCustomDestination(item, destinationUri)) {
                is AppResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isExporting = false,
                        exportSuccessMessage = "Exported file successfully to destination!"
                    )
                }
                is AppResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isExporting = false,
                        errorMessage = result.error.message
                    )
                }
            }
        }
    }

    fun getShareIntent(): Intent? {
        val item = _uiState.value.mediaItem ?: return null
        return mediaExporter.createShareIntent(item)
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(exportSuccessMessage = null, errorMessage = null)
    }
}
