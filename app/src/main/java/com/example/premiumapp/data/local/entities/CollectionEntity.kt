package com.example.premiumapp.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.premiumapp.domain.model.CollectionItem

@Entity(
    tableName = "collections",
    indices = [Index("name")]
)
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val coverUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(mediaCount: Int = 0): CollectionItem = CollectionItem(
        id = id,
        name = name,
        description = description,
        coverUri = coverUri,
        mediaCount = mediaCount,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(item: CollectionItem): CollectionEntity = CollectionEntity(
            id = item.id,
            name = item.name,
            description = item.description,
            coverUri = item.coverUri,
            createdAt = item.createdAt,
            updatedAt = item.updatedAt
        )
    }
}
