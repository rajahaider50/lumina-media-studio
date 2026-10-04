package com.example.premiumapp.domain.model

data class StorageStats(
    val totalStorageBytes: Long = 0L,
    val freeStorageBytes: Long = 0L,
    val usedStorageBytes: Long = 0L,
    val appStorageBytes: Long = 0L,
    val mediaStorageBytes: Long = 0L,
    val cacheSizeBytes: Long = 0L
) {
    val deviceUsedPercent: Float
        get() = if (totalStorageBytes > 0) usedStorageBytes.toFloat() / totalStorageBytes.toFloat() else 0f

    val formattedTotalSpace: String
        get() = StorageInfo.formatBytes(totalStorageBytes)

    val formattedFreeSpace: String
        get() = StorageInfo.formatBytes(freeStorageBytes)

    val formattedUsedSpace: String
        get() = StorageInfo.formatBytes(usedStorageBytes)

    val formattedMediaSpace: String
        get() = StorageInfo.formatBytes(mediaStorageBytes)

    val formattedCacheSpace: String
        get() = StorageInfo.formatBytes(cacheSizeBytes)

    fun toStorageInfo(): StorageInfo = StorageInfo(
        totalDeviceBytes = totalStorageBytes,
        freeDeviceBytes = freeStorageBytes,
        appMediaBytes = mediaStorageBytes,
        appCacheBytes = cacheSizeBytes
    )
}

data class StorageInfo(
    val totalDeviceBytes: Long,
    val freeDeviceBytes: Long,
    val appMediaBytes: Long,
    val appCacheBytes: Long
) {
    val usedDeviceBytes: Long
        get() = (totalDeviceBytes - freeDeviceBytes).coerceAtLeast(0L)

    val deviceUsedPercent: Float
        get() = if (totalDeviceBytes > 0) usedDeviceBytes.toFloat() / totalDeviceBytes.toFloat() else 0f

    val formattedAppMedia: String
        get() = formatBytes(appMediaBytes)

    val formattedAppCache: String
        get() = formatBytes(appCacheBytes)

    val formattedFreeSpace: String
        get() = formatBytes(freeDeviceBytes)

    val formattedTotalSpace: String
        get() = formatBytes(totalDeviceBytes)

    companion object {
        fun formatBytes(bytes: Long): String {
            val kb = bytes / 1024.0
            val mb = kb / 1024.0
            val gb = mb / 1024.0
            return when {
                gb >= 1.0 -> String.format("%.2f GB", gb)
                mb >= 1.0 -> String.format("%.1f MB", mb)
                kb >= 1.0 -> String.format("%.0f KB", kb)
                else -> "$bytes B"
            }
        }
    }
}
