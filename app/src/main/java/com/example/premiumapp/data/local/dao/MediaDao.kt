package com.example.premiumapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.premiumapp.data.local.entities.MediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM media ORDER BY dateAdded DESC")
    fun getAllMedia(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media ORDER BY dateAdded DESC")
    suspend fun getAllMediaList(): List<MediaEntity>

    @Query("SELECT * FROM media WHERE id = :id LIMIT 1")
    fun getMediaById(id: Long): Flow<MediaEntity?>

    @Query("SELECT * FROM media WHERE id = :id LIMIT 1")
    suspend fun getMediaByIdDirect(id: Long): MediaEntity?

    @Query("SELECT * FROM media WHERE uri = :uri LIMIT 1")
    suspend fun getMediaByUri(uri: String): MediaEntity?

    @Query("SELECT * FROM media WHERE isFavorite = 1 ORDER BY dateAdded DESC")
    fun getFavorites(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media ORDER BY dateAdded DESC LIMIT :limit")
    fun getRecentMedia(limit: Int): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media WHERE mediaType = :mediaType ORDER BY dateAdded DESC")
    fun getMediaByType(mediaType: String): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media WHERE displayName LIKE '%' || :query || '%' ORDER BY dateAdded DESC")
    fun searchMedia(query: String): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media ORDER BY CASE WHEN :sortMode = 'NEWEST' THEN dateAdded END DESC, CASE WHEN :sortMode = 'OLDEST' THEN dateAdded END ASC, CASE WHEN :sortMode = 'NAME_AZ' THEN displayName END ASC, CASE WHEN :sortMode = 'NAME_ZA' THEN displayName END DESC, CASE WHEN :sortMode = 'LARGEST' THEN sizeBytes END DESC, CASE WHEN :sortMode = 'SMALLEST' THEN sizeBytes END ASC")
    fun getMediaSorted(sortMode: String): Flow<List<MediaEntity>>

    @Query("SELECT COUNT(*) FROM media")
    fun getMediaCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM media WHERE isFavorite = 1")
    fun getFavoritesCount(): Flow<Int>

    @Query("SELECT SUM(sizeBytes) FROM media")
    fun getTotalMediaSize(): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(media: MediaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mediaList: List<MediaEntity>): List<Long>

    @Update
    suspend fun update(media: MediaEntity)

    @Query("UPDATE media SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Delete
    suspend fun delete(media: MediaEntity)

    @Query("DELETE FROM media WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM media WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)
}
