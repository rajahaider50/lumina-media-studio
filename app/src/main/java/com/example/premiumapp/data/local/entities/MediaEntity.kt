package com.example.premiumapp.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.model.MediaType

@Entity(
    tableName = "media",
    indices = [
        Index("displayName"),
        Index("mimeType"),
        Index("mediaType"),
        Index("dateAdded"),
        Index("dateModified"),
        Index("isFavorite")
    ]
)
data class MediaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uri: String,
    val localPath: String? = null,
    val displayName: String,
    val mimeType: String,
    val mediaType: String, // "IMAGE" or "VIDEO"
    val sizeBytes: Long,
    val width: Int? = null,
    val height: Int? = null,
    val durationMs: Long? = null,
    val dateAdded: Long = System.currentTimeMillis(),
    val dateModified: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val thumbnailUri: String? = null,
    val hash: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): MediaItem = MediaItem(
        id = id,
        uri = uri,
        localPath = localPath,
        displayName = displayName,
        mimeType = mimeType,
        mediaType = if (mediaType == "VIDEO") MediaType.VIDEO else MediaType.IMAGE,
        sizeBytes = sizeBytes,
        width = width,
        height = height,
        durationMs = durationMs,
        dateAdded = dateAdded,
        dateModified = dateModified,
        isFavorite = isFavorite,
        thumbnailUri = thumbnailUri,
        hash = hash
    )

    companion object {
        fun fromDomain(item: MediaItem): MediaEntity = MediaEntity(
            id = item.id,
            uri = item.uri,
            localPath = item.localPath,
            displayName = item.displayName,
            mimeType = item.mimeType,
            mediaType = item.mediaType.name,
            sizeBytes = item.sizeBytes,
            width = item.width,
            height = item.height,
            durationMs = item.durationMs,
            dateAdded = item.dateAdded,
            dateModified = item.dateModified,
            isFavorite = item.isFavorite,
            thumbnailUri = item.thumbnailUri,
            hash = item.hash
        )
    }
}
