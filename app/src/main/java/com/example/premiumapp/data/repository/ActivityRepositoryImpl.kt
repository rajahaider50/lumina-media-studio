package com.example.premiumapp.data.repository

import com.example.premiumapp.data.local.dao.ActivityDao
import com.example.premiumapp.data.local.entities.ActivityEntity
import com.example.premiumapp.domain.model.ActivityItem
import com.example.premiumapp.domain.model.ActivityType
import com.example.premiumapp.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ActivityRepositoryImpl(
    private val activityDao: ActivityDao
) : ActivityRepository {

    override fun getRecentActivities(limit: Int): Flow<List<ActivityItem>> {
        return activityDao.getRecentActivities(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun logActivity(
        type: ActivityType,
        description: String,
        mediaId: Long?,
        collectionId: Long?
    ) {
        val entity = ActivityEntity(
            type = type.name,
            mediaId = mediaId,
            collectionId = collectionId,
            description = description,
            timestamp = System.currentTimeMillis()
        )
        activityDao.insert(entity)
        activityDao.trimOldActivities(200)
    }

    override suspend fun clearActivities() {
        activityDao.clearAll()
    }
}
