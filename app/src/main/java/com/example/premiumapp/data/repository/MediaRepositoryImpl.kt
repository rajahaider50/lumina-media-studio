package com.example.premiumapp.data.repository

import android.net.Uri
import com.example.premiumapp.core.common.AppResult
import com.example.premiumapp.core.media.ImageEditParams
import com.example.premiumapp.core.media.MediaEditor
import com.example.premiumapp.core.media.MediaExporter
import com.example.premiumapp.core.media.MediaImporter
import com.example.premiumapp.data.local.dao.MediaDao
import com.example.premiumapp.data.local.entities.MediaEntity
import com.example.premiumapp.domain.model.ActivityType
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.model.MediaType
import com.example.premiumapp.domain.model.SortMode
import com.example.premiumapp.domain.repository.ActivityRepository
import com.example.premiumapp.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

class MediaRepositoryImpl(
    private val mediaDao: MediaDao,
    private val mediaImporter: MediaImporter,
    private val mediaExporter: MediaExporter,
    private val mediaEditor: MediaEditor,
    private val activityRepository: ActivityRepository
) : MediaRepository {

    override fun getAllMedia(): Flow<List<MediaItem>> {
        return mediaDao.getAllMedia().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getMediaById(id: Long): Flow<MediaItem?> {
        return mediaDao.getMediaById(id).map { it?.toDomain() }
    }

    override suspend fun getMediaByIdDirect(id: Long): MediaItem? {
        return mediaDao.getMediaByIdDirect(id)?.toDomain()
    }

    override fun getFavorites(): Flow<List<MediaItem>> {
        return mediaDao.getFavorites().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getRecentMedia(limit: Int): Flow<List<MediaItem>> {
        return mediaDao.getRecentMedia(limit).map { entities -> entities.map { it.toDomain() } }
    }

    override fun searchMedia(query: String): Flow<List<MediaItem>> {
        return mediaDao.searchMedia(query).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getMediaByType(mediaType: MediaType): Flow<List<MediaItem>> {
        return mediaDao.getMediaByType(mediaType.name).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getMediaSorted(sortMode: SortMode): Flow<List<MediaItem>> {
        return mediaDao.getMediaSorted(sortMode.name).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun importMediaFromUri(uri: Uri): AppResult<MediaItem> {
        return when (val result = mediaImporter.importMedia(uri)) {
            is AppResult.Success -> {
                val mediaItem = result.data
                val entity = MediaEntity.fromDomain(mediaItem)
                val newId = mediaDao.insert(entity)
                val savedItem = mediaItem.copy(id = newId)

                activityRepository.logActivity(
                    type = ActivityType.IMPORTED,
                    description = "Imported ${mediaItem.displayName}",
                    mediaId = newId
                )
                AppResult.Success(savedItem)
            }
            is AppResult.Error -> result
        }
    }

    override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        mediaDao.setFavorite(id, isFavorite)
        val item = mediaDao.getMediaByIdDirect(id)
        if (item != null) {
            activityRepository.logActivity(
                type = if (isFavorite) ActivityType.FAVORITED else ActivityType.UNFAVORITED,
                description = if (isFavorite) "Marked ${item.displayName} as favorite" else "Removed ${item.displayName} from favorites",
                mediaId = id
            )
        }
    }

    override suspend fun deleteMedia(id: Long): Boolean {
        val item = mediaDao.getMediaByIdDirect(id) ?: return false
        item.localPath?.let { path ->
            val file = File(path)
            if (file.exists()) file.delete()
        }
        item.thumbnailUri?.let { thumbUri ->
            if (thumbUri.startsWith("file://")) {
                val thumbFile = File(thumbUri.removePrefix("file://"))
                if (thumbFile.exists()) thumbFile.delete()
            }
        }
        mediaDao.deleteById(id)
        activityRepository.logActivity(
            type = ActivityType.DELETED,
            description = "Deleted ${item.displayName}",
            mediaId = id
        )
        return true
    }

    override suspend fun deleteMediaBatch(ids: List<Long>): Boolean {
        for (id in ids) {
            deleteMedia(id)
        }
        return true
    }

    override suspend fun applyImageEdits(original: MediaItem, params: ImageEditParams): AppResult<MediaItem> {
        return when (val result = mediaEditor.applyImageEditsAndSaveCopy(original, params)) {
            is AppResult.Success -> {
                val editedItem = result.data
                val entity = MediaEntity.fromDomain(editedItem)
                val newId = mediaDao.insert(entity)
                val savedItem = editedItem.copy(id = newId)

                activityRepository.logActivity(
                    type = ActivityType.EDITED,
                    description = "Edited copy of ${original.displayName}",
                    mediaId = newId
                )
                AppResult.Success(savedItem)
            }
            is AppResult.Error -> result
        }
    }

    override suspend fun trimVideo(original: MediaItem, startMs: Long, endMs: Long): AppResult<MediaItem> {
        return when (val result = mediaEditor.trimVideoAndSaveCopy(original, startMs, endMs)) {
            is AppResult.Success -> {
                val trimmedItem = result.data
                val entity = MediaEntity.fromDomain(trimmedItem)
                val newId = mediaDao.insert(entity)
                val savedItem = trimmedItem.copy(id = newId)

                activityRepository.logActivity(
                    type = ActivityType.EDITED,
                    description = "Trimmed copy of ${original.displayName}",
                    mediaId = newId
                )
                AppResult.Success(savedItem)
            }
            is AppResult.Error -> result
        }
    }

    override suspend fun exportToGallery(mediaItem: MediaItem): AppResult<Uri> {
        val result = mediaExporter.exportToGallery(mediaItem)
        if (result is AppResult.Success) {
            activityRepository.logActivity(
                type = ActivityType.EXPORTED,
                description = "Exported ${mediaItem.displayName} to device gallery",
                mediaId = mediaItem.id
            )
        }
        return result
    }

    override suspend fun exportToDestinationUri(mediaItem: MediaItem, dest: Uri): AppResult<Unit> {
        val result = mediaExporter.exportToDestinationUri(mediaItem, dest)
        if (result is AppResult.Success) {
            activityRepository.logActivity(
                type = ActivityType.EXPORTED,
                description = "Exported ${mediaItem.displayName} to selected folder",
                mediaId = mediaItem.id
            )
        }
        return result
    }

    override fun getMediaCount(): Flow<Int> = mediaDao.getMediaCount()
    override fun getFavoritesCount(): Flow<Int> = mediaDao.getFavoritesCount()
    override fun getTotalMediaSize(): Flow<Long?> = mediaDao.getTotalMediaSize()
}
