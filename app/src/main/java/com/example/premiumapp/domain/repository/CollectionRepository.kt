package com.example.premiumapp.domain.repository

import com.example.premiumapp.domain.model.CollectionItem
import com.example.premiumapp.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {
    fun getAllCollections(): Flow<List<CollectionItem>>
    fun getCollectionById(id: Long): Flow<CollectionItem?>
    suspend fun getCollectionByIdDirect(id: Long): CollectionItem?
    fun getMediaForCollection(collectionId: Long): Flow<List<MediaItem>>
    suspend fun createCollection(name: String, description: String? = null, coverUri: String? = null): Long
    suspend fun updateCollection(collection: CollectionItem)
    suspend fun deleteCollection(id: Long)
    suspend fun addMediaToCollection(collectionId: Long, mediaId: Long)
    suspend fun addMediaBatchToCollection(collectionId: Long, mediaIds: List<Long>)
    suspend fun removeMediaFromCollection(collectionId: Long, mediaId: Long)
    suspend fun removeMediaBatchFromCollection(collectionId: Long, mediaIds: List<Long>)
    fun getCollectionsCount(): Flow<Int>
}
