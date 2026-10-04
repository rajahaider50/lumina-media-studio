package com.example.premiumapp.core.storage

import android.content.Context
import android.os.Environment
import android.os.StatFs
import com.example.premiumapp.domain.model.StorageStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class StorageManager(private val context: Context) {

    suspend fun getStorageStats(): StorageStats = withContext(Dispatchers.IO) {
        val statFs = StatFs(Environment.getDataDirectory().path)
        val blockSize = statFs.blockSizeLong
        val totalBlocks = statFs.blockCountLong
        val availableBlocks = statFs.availableBlocksLong

        val totalStorageBytes = totalBlocks * blockSize
        val freeStorageBytes = availableBlocks * blockSize
        val usedStorageBytes = totalStorageBytes - freeStorageBytes

        val appInternalDir = context.filesDir
        val appMediaDir = File(appInternalDir, "media")
        val appCacheDir = context.cacheDir
        val externalCacheDir = context.externalCacheDir

        val appStorageBytes = getDirectorySize(appInternalDir)
        val mediaStorageBytes = if (appMediaDir.exists()) getDirectorySize(appMediaDir) else 0L
        val cacheSizeBytes = getDirectorySize(appCacheDir) + (externalCacheDir?.let { getDirectorySize(it) } ?: 0L)

        StorageStats(
            totalStorageBytes = totalStorageBytes,
            freeStorageBytes = freeStorageBytes,
            usedStorageBytes = usedStorageBytes,
            appStorageBytes = appStorageBytes,
            mediaStorageBytes = mediaStorageBytes,
            cacheSizeBytes = cacheSizeBytes
        )
    }

    suspend fun clearCache(): Boolean = withContext(Dispatchers.IO) {
        try {
            deleteDirectoryContents(context.cacheDir)
            context.externalCacheDir?.let { deleteDirectoryContents(it) }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun hasEnoughStorage(requiredBytes: Long = 20 * 1024 * 1024L): Boolean = withContext(Dispatchers.IO) {
        try {
            val statFs = StatFs(Environment.getDataDirectory().path)
            val availableBytes = statFs.availableBlocksLong * statFs.blockSizeLong
            availableBytes > requiredBytes
        } catch (e: Exception) {
            true
        }
    }

    private fun getDirectorySize(dir: File?): Long {
        if (dir == null || !dir.exists()) return 0L
        var totalSize = 0L
        val files = dir.listFiles() ?: return 0L
        for (file in files) {
            totalSize += if (file.isDirectory) {
                getDirectorySize(file)
            } else {
                file.length()
            }
        }
        return totalSize
    }

    private fun deleteDirectoryContents(dir: File?) {
        if (dir == null || !dir.exists() || !dir.isDirectory) return
        val files = dir.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory) {
                deleteDirectoryContents(file)
            }
            file.delete()
        }
    }
}
