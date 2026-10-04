package com.example.premiumapp.domain.usecase

import com.example.premiumapp.domain.model.CollectionItem
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow

class CreateCollectionUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(name: String, description: String? = null, coverUri: String? = null): Long =
        repository.createCollection(name, description, coverUri)
}

class DeleteCollectionUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteCollection(id)
}

class AddMediaToCollectionUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(collectionId: Long, mediaId: Long) =
        repository.addMediaToCollection(collectionId, mediaId)

    suspend fun addBatch(collectionId: Long, mediaIds: List<Long>) =
        repository.addMediaBatchToCollection(collectionId, mediaIds)
}

class RemoveMediaFromCollectionUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(collectionId: Long, mediaId: Long) =
        repository.removeMediaFromCollection(collectionId, mediaId)

    suspend fun removeBatch(collectionId: Long, mediaIds: List<Long>) =
        repository.removeMediaBatchFromCollection(collectionId, mediaIds)
}

class GetCollectionMediaUseCase(private val repository: CollectionRepository) {
    operator fun invoke(collectionId: Long): Flow<List<MediaItem>> =
        repository.getMediaForCollection(collectionId)
}
