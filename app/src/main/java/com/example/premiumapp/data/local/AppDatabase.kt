package com.example.premiumapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.premiumapp.data.local.dao.ActivityDao
import com.example.premiumapp.data.local.dao.CollectionDao
import com.example.premiumapp.data.local.dao.MediaDao
import com.example.premiumapp.data.local.dao.RecentSearchDao
import com.example.premiumapp.data.local.entities.ActivityEntity
import com.example.premiumapp.data.local.entities.CollectionEntity
import com.example.premiumapp.data.local.entities.CollectionMediaCrossRef
import com.example.premiumapp.data.local.entities.MediaEntity
import com.example.premiumapp.data.local.entities.RecentSearchEntity

@Database(
    entities = [
        MediaEntity::class,
        CollectionEntity::class,
        CollectionMediaCrossRef::class,
        ActivityEntity::class,
        RecentSearchEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
    abstract fun collectionDao(): CollectionDao
    abstract fun activityDao(): ActivityDao
    abstract fun recentSearchDao(): RecentSearchDao

    companion object {
        const val DATABASE_NAME = "lumina_media.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()
        }
    }
}
