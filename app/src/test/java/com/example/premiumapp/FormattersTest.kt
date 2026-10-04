package com.example.premiumapp

import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.model.MediaType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FormattersTest {

    @Test
    fun testFormattedDuration() {
        val itemNoDuration = MediaItem(
            id = 1,
            uri = "content://1",
            localPath = "/data/1.jpg",
            displayName = "photo.jpg",
            mimeType = "image/jpeg",
            mediaType = MediaType.IMAGE,
            sizeBytes = 1000L,
            width = 100,
            height = 100,
            durationMs = null,
            dateAdded = 0L,
            dateModified = 0L
        )
        assertNull(itemNoDuration.formattedDuration)

        val itemVideo = itemNoDuration.copy(
            mediaType = MediaType.VIDEO,
            durationMs = 125000L // 2m 5s
        )
        assertEquals("02:05", itemVideo.formattedDuration)

        val itemZeroSec = itemNoDuration.copy(
            mediaType = MediaType.VIDEO,
            durationMs = 60000L // 1m 0s
        )
        assertEquals("01:00", itemZeroSec.formattedDuration)
    }

    @Test
    fun testResolutionText() {
        val item = MediaItem(
            id = 1,
            uri = "content://1",
            localPath = "/data/1.jpg",
            displayName = "photo.jpg",
            mimeType = "image/jpeg",
            mediaType = MediaType.IMAGE,
            sizeBytes = 1000L,
            width = 1920,
            height = 1080,
            dateAdded = 0L,
            dateModified = 0L
        )
        assertEquals("1920 × 1080", item.resolutionText)

        val itemUnknown = item.copy(width = 0, height = 0)
        assertEquals("Unknown resolution", itemUnknown.resolutionText)
    }
}
