package com.example.premiumapp.data.repository

import com.example.premiumapp.data.local.dao.CollectionDao
import com.example.premiumapp.data.local.entities.CollectionEntity
import com.example.premiumapp.data.local.entities.CollectionMediaCrossRef
import com.example.premiumapp.domain.model.ActivityType
import com.example.premiumapp.domain.model.CollectionItem
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.repository.ActivityRepository
import com.example.premiumapp.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CollectionRepositoryImpl(
    private val collectionDao: CollectionDao,
    private val activityRepository: ActivityRepository
) : CollectionRepository {

    override fun getAllCollections(): Flow<List<CollectionItem>> {
        return collectionDao.getAllCollections().map { list ->
            list.map { entity ->
                val count = collectionDao.getMediaCountForCollectionDirect(entity.id)
                entity.toDomain(count)
            }
        }
    }

    override fun getCollectionById(id: Long): Flow<CollectionItem?> {
        return collectionDao.getCollectionById(id).map { entity ->
            if (entity != null) {
                val count = collectionDao.getMediaCountForCollectionDirect(entity.id)
                entity.toDomain(count)
            } else null
        }
    }

    override suspend fun getCollectionByIdDirect(id: Long): CollectionItem? {
        val entity = collectionDao.getCollectionByIdDirect(id) ?: return null
        val count = collectionDao.getMediaCountForCollectionDirect(entity.id)
        return entity.toDomain(count)
    }

    override fun getMediaForCollection(collectionId: Long): Flow<List<MediaItem>> {
        return collectionDao.getMediaForCollection(collectionId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createCollection(name: String, description: String?, coverUri: String?): Long {
        val entity = CollectionEntity(
            name = name,
            description = description,
            coverUri = coverUri,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val id = collectionDao.insert(entity)
        activityRepository.logActivity(
            type = ActivityType.CREATED_COLLECTION,
            description = "Created collection '$name'",
            collectionId = id
        )
        return id
    }

    override suspend fun updateCollection(collection: CollectionItem) {
        val entity = CollectionEntity.fromDomain(collection).copy(updatedAt = System.currentTimeMillis())
        collectionDao.update(entity)
    }

    override suspend fun deleteCollection(id: Long) {
        val collection = collectionDao.getCollectionByIdDirect(id)
        collectionDao.deleteById(id)
        if (collection != null) {
            activityRepository.logActivity(
                type = ActivityType.DELETED,
                description = "Deleted collection '${collection.name}'"
            )
        }
    }

    override suspend fun addMediaToCollection(collectionId: Long, mediaId: Long) {
        collectionDao.insertCrossRef(
            CollectionMediaCrossRef(
                collectionId = collectionId,
                mediaId = mediaId,
                addedAt = System.currentTimeMillis()
            )
        )
        val collection = collectionDao.getCollectionByIdDirect(collectionId)
        if (collection != null) {
            activityRepository.logActivity(
                type = ActivityType.ADDED_TO_COLLECTION,
                description = "Added media to '${collection.name}'",
                mediaId = mediaId,
                collectionId = collectionId
            )
        }
    }

    override suspend fun addMediaBatchToCollection(collectionId: Long, mediaIds: List<Long>) {
        val now = System.currentTimeMillis()
        val refs = mediaIds.map { mediaId ->
            CollectionMediaCrossRef(collectionId = collectionId, mediaId = mediaId, addedAt = now)
        }
        collectionDao.insertCrossRefs(refs)
        val collection = collectionDao.getCollectionByIdDirect(collectionId)
        if (collection != null) {
            activityRepository.logActivity(
                type = ActivityType.ADDED_TO_COLLECTION,
                description = "Added ${mediaIds.size} items to '${collection.name}'",
                collectionId = collectionId
            )
        }
    }

    override suspend fun removeMediaFromCollection(collectionId: Long, mediaId: Long) {
        collectionDao.removeMediaFromCollection(collectionId, mediaId)
        val collection = collectionDao.getCollectionByIdDirect(collectionId)
        if (collection != null) {
            activityRepository.logActivity(
                type = ActivityType.REMOVED_FROM_COLLECTION,
                description = "Removed media from '${collection.name}'",
                mediaId = mediaId,
                collectionId = collectionId
            )
        }
    }

    override suspend fun removeMediaBatchFromCollection(collectionId: Long, mediaIds: List<Long>) {
        collectionDao.removeMediaItemsFromCollection(collectionId, mediaIds)
    }

    override fun getCollectionsCount(): Flow<Int> = collectionDao.getCollectionsCount()
}
