package com.example.premiumapp.feature.importmedia

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.core.common.AppResult
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.usecase.ImportMediaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ImportMediaUiState(
    val isImporting: Boolean = false,
    val currentItemIndex: Int = 0,
    val totalItems: Int = 0,
    val successfulImports: List<MediaItem> = emptyList(),
    val failedCount: Int = 0,
    val errorMessage: String? = null
)

class ImportMediaViewModel(
    private val importMediaUseCase: ImportMediaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportMediaUiState())
    val uiState: StateFlow<ImportMediaUiState> = _uiState.asStateFlow()

    fun importUris(uris: List<Uri>, onAllCompleted: (() -> Unit)? = null) {
        if (uris.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = ImportMediaUiState(
                isImporting = true,
                currentItemIndex = 0,
                totalItems = uris.size
            )

            val successful = mutableListOf<MediaItem>()
            var failed = 0

            uris.forEachIndexed { index, uri ->
                _uiState.value = _uiState.value.copy(currentItemIndex = index + 1)
                when (val result = importMediaUseCase(uri)) {
                    is AppResult.Success -> successful.add(result.data)
                    is AppResult.Error -> failed++
                }
            }

            _uiState.value = _uiState.value.copy(
                isImporting = false,
                successfulImports = successful,
                failedCount = failed
            )

            onAllCompleted?.invoke()
        }
    }

    fun resetState() {
        _uiState.value = ImportMediaUiState()
    }
}
