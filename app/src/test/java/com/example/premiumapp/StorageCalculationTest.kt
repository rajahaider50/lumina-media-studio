package com.example.premiumapp

import com.example.premiumapp.domain.model.StorageInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StorageCalculationTest {

    @Test
    fun testByteFormatting() {
        assertEquals("500 B", StorageInfo.formatBytes(500L))
        assertEquals("10 KB", StorageInfo.formatBytes(10 * 1024L))
        assertEquals("15.5 MB", StorageInfo.formatBytes((15.5 * 1024 * 1024).toLong()))
        assertEquals("2.50 GB", StorageInfo.formatBytes((2.5 * 1024 * 1024 * 1024).toLong()))
    }

    @Test
    fun testStoragePercentages() {
        val total = 100_000_000_000L // 100 GB
        val free = 25_000_000_000L   // 25 GB
        val appMedia = 5_000_000_000L // 5 GB
        val appCache = 500_000_000L  // 500 MB

        val storageInfo = StorageInfo(
            totalDeviceBytes = total,
            freeDeviceBytes = free,
            appMediaBytes = appMedia,
            appCacheBytes = appCache
        )

        assertEquals(75_000_000_000L, storageInfo.usedDeviceBytes)
        assertEquals(0.75f, storageInfo.deviceUsedPercent, 0.001f)
    }
}
