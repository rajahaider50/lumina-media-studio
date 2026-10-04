package com.example.premiumapp.domain.usecase

import android.net.Uri
import com.example.premiumapp.core.common.AppResult
import com.example.premiumapp.core.media.ImageEditParams
import com.example.premiumapp.core.storage.StorageManager
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.model.StorageStats
import com.example.premiumapp.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow

class ImportMediaUseCase(private val repository: MediaRepository) {
    suspend operator fun invoke(uri: Uri): AppResult<MediaItem> = repository.importMediaFromUri(uri)
}

class DeleteMediaUseCase(private val repository: MediaRepository) {
    suspend operator fun invoke(id: Long): Boolean = repository.deleteMedia(id)
    suspend fun deleteBatch(ids: List<Long>): Boolean = repository.deleteMediaBatch(ids)
}

class ToggleFavoriteUseCase(private val repository: MediaRepository) {
    suspend operator fun invoke(id: Long, isFavorite: Boolean) = repository.toggleFavorite(id, isFavorite)
}

class SearchMediaUseCase(private val repository: MediaRepository) {
    operator fun invoke(query: String): Flow<List<MediaItem>> = repository.searchMedia(query)
}

class ExportMediaUseCase(private val repository: MediaRepository) {
    suspend fun toGallery(mediaItem: MediaItem): AppResult<Uri> = repository.exportToGallery(mediaItem)
    suspend fun toCustomDestination(mediaItem: MediaItem, destinationUri: Uri): AppResult<Unit> =
        repository.exportToDestinationUri(mediaItem, destinationUri)
}

class EditMediaUseCase(private val repository: MediaRepository) {
    suspend fun editImage(original: MediaItem, params: ImageEditParams): AppResult<MediaItem> =
        repository.applyImageEdits(original, params)

    suspend fun trimVideo(original: MediaItem, startMs: Long, endMs: Long): AppResult<MediaItem> =
        repository.trimVideo(original, startMs, endMs)
}

class GetStorageInfoUseCase(private val storageManager: StorageManager) {
    suspend operator fun invoke(): StorageStats = storageManager.getStorageStats()
    suspend fun clearCache(): Boolean = storageManager.clearCache()
}
