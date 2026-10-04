package com.example.premiumapp.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.premiumapp.domain.model.ActivityItem
import com.example.premiumapp.domain.model.ActivityType

@Entity(
    tableName = "activities",
    indices = [
        Index("timestamp"),
        Index("type")
    ]
)
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // from ActivityType enum name
    val mediaId: Long? = null,
    val collectionId: Long? = null,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): ActivityItem = ActivityItem(
        id = id,
        type = try {
            ActivityType.valueOf(type)
        } catch (e: Exception) {
            ActivityType.OPENED
        },
        mediaId = mediaId,
        collectionId = collectionId,
        description = description,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(item: ActivityItem): ActivityEntity = ActivityEntity(
            id = item.id,
            type = item.type.name,
            mediaId = item.mediaId,
            collectionId = item.collectionId,
            description = item.description,
            timestamp = item.timestamp
        )
    }
}
