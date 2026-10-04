package com.example.premiumapp.feature.importmedia

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.core.common.AppResult
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.usecase.ImportMediaUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

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

    fun seedDemoAssets(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val sampleUris = mutableListOf<Uri>()
            val demos = listOf(
                Triple("Neon Cyberpunk Skyline", 0xFF0F172A.toInt(), 0xFF06B6D4.toInt()),
                Triple("Golden Sunset Panorama", 0xFF7C2D12.toInt(), 0xFFF59E0B.toInt()),
                Triple("Deep Space Nebula", 0xFF1E1B4B.toInt(), 0xFF8B5CF6.toInt())
            )
            demos.forEach { (name, colorStart, colorEnd) ->
                val bitmap = Bitmap.createBitmap(1080, 1080, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                val paint = Paint().apply {
                    shader = LinearGradient(
                        0f, 0f, 1080f, 1080f,
                        colorStart, colorEnd,
                        Shader.TileMode.CLAMP
                    )
                }
                canvas.drawRect(0f, 0f, 1080f, 1080f, paint)

                val textPaint = Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 52f
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText(name, 540f, 540f, textPaint)

                val file = File(context.cacheDir, "${name.replace(' ', '_')}.jpg")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
                }
                bitmap.recycle()
                sampleUris.add(Uri.fromFile(file))
            }
            withContext(Dispatchers.Main) {
                importUris(sampleUris)
            }
        }
    }

    fun resetState() {
        _uiState.value = ImportMediaUiState()
    }
}
