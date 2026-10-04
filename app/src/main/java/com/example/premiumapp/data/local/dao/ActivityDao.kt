package com.example.premiumapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.premiumapp.data.local.entities.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentActivities(limit: Int = 100): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: ActivityEntity): Long

    @Query("DELETE FROM activities")
    suspend fun clearAll()

    @Query("DELETE FROM activities WHERE id NOT IN (SELECT id FROM activities ORDER BY timestamp DESC LIMIT :keepCount)")
    suspend fun trimOldActivities(keepCount: Int = 200)
}
