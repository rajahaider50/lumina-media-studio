package com.example.premiumapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.premiumapp.data.local.entities.CollectionEntity
import com.example.premiumapp.data.local.entities.CollectionMediaCrossRef
import com.example.premiumapp.data.local.entities.MediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collections ORDER BY updatedAt DESC")
    fun getAllCollections(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collections WHERE id = :id LIMIT 1")
    fun getCollectionById(id: Long): Flow<CollectionEntity?>

    @Query("SELECT * FROM collections WHERE id = :id LIMIT 1")
    suspend fun getCollectionByIdDirect(id: Long): CollectionEntity?

    @Query("""
        SELECT m.* FROM media m
        INNER JOIN collection_media_cross_ref ref ON m.id = ref.mediaId
        WHERE ref.collectionId = :collectionId
        ORDER BY ref.addedAt DESC
    """)
    fun getMediaForCollection(collectionId: Long): Flow<List<MediaEntity>>

    @Query("SELECT COUNT(*) FROM collections")
    fun getCollectionsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM collection_media_cross_ref WHERE collectionId = :collectionId")
    fun getMediaCountForCollection(collectionId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM collection_media_cross_ref WHERE collectionId = :collectionId")
    suspend fun getMediaCountForCollectionDirect(collectionId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collection: CollectionEntity): Long

    @Update
    suspend fun update(collection: CollectionEntity)

    @Delete
    suspend fun delete(collection: CollectionEntity)

    @Query("DELETE FROM collections WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: CollectionMediaCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRefs(crossRefs: List<CollectionMediaCrossRef>)

    @Query("DELETE FROM collection_media_cross_ref WHERE collectionId = :collectionId AND mediaId = :mediaId")
    suspend fun removeMediaFromCollection(collectionId: Long, mediaId: Long)

    @Query("DELETE FROM collection_media_cross_ref WHERE collectionId = :collectionId AND mediaId IN (:mediaIds)")
    suspend fun removeMediaItemsFromCollection(collectionId: Long, mediaIds: List<Long>)

    @Query("SELECT collectionId FROM collection_media_cross_ref WHERE mediaId = :mediaId")
    fun getCollectionIdsForMedia(mediaId: Long): Flow<List<Long>>
}
