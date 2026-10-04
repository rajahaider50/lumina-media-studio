package com.example.premiumapp.domain.repository

import android.net.Uri
import com.example.premiumapp.core.common.AppResult
import com.example.premiumapp.core.media.ImageEditParams
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.model.MediaType
import com.example.premiumapp.domain.model.SortMode
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getAllMedia(): Flow<List<MediaItem>>
    fun getMediaById(id: Long): Flow<MediaItem?>
    suspend fun getMediaByIdDirect(id: Long): MediaItem?
    fun getFavorites(): Flow<List<MediaItem>>
    fun getRecentMedia(limit: Int): Flow<List<MediaItem>>
    fun searchMedia(query: String): Flow<List<MediaItem>>
    fun getMediaByType(mediaType: MediaType): Flow<List<MediaItem>>
    fun getMediaSorted(sortMode: SortMode): Flow<List<MediaItem>>
    suspend fun importMediaFromUri(uri: Uri): AppResult<MediaItem>
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
    suspend fun deleteMedia(id: Long): Boolean
    suspend fun deleteMediaBatch(ids: List<Long>): Boolean
    suspend fun applyImageEdits(original: MediaItem, params: ImageEditParams): AppResult<MediaItem>
    suspend fun trimVideo(original: MediaItem, startMs: Long, endMs: Long): AppResult<MediaItem>
    suspend fun exportToGallery(mediaItem: MediaItem): AppResult<Uri>
    suspend fun exportToDestinationUri(mediaItem: MediaItem, dest: Uri): AppResult<Unit>
    fun getMediaCount(): Flow<Int>
    fun getFavoritesCount(): Flow<Int>
    fun getTotalMediaSize(): Flow<Long?>
}
