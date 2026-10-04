package com.example.premiumapp.domain.model

data class ActivityItem(
    val id: Long = 0,
    val type: ActivityType,
    val mediaId: Long? = null,
    val collectionId: Long? = null,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val mediaThumbnailUri: String? = null
)
