package com.example.premiumapp.domain.repository

import com.example.premiumapp.domain.model.ActivityItem
import com.example.premiumapp.domain.model.ActivityType
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun getRecentActivities(limit: Int = 100): Flow<List<ActivityItem>>
    suspend fun logActivity(type: ActivityType, description: String, mediaId: Long? = null, collectionId: Long? = null)
    suspend fun clearActivities()
}
