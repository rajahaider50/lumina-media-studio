package com.example.premiumapp

import com.example.premiumapp.domain.model.FilterOption
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.model.MediaType
import com.example.premiumapp.domain.model.SortOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SortAndFilterTest {

    private val sampleItems = listOf(
        MediaItem(
            id = 1,
            uri = "content://1",
            localPath = "/data/1.jpg",
            displayName = "banana.jpg",
            mimeType = "image/jpeg",
            mediaType = MediaType.IMAGE,
            sizeBytes = 2 * 1024 * 1024, // 2MB
            width = 1920,
            height = 1080,
            dateAdded = 1000L,
            dateModified = 1000L,
            isFavorite = true
        ),
        MediaItem(
            id = 2,
            uri = "content://2",
            localPath = "/data/2.mp4",
            displayName = "apple.mp4",
            mimeType = "video/mp4",
            mediaType = MediaType.VIDEO,
            sizeBytes = 15 * 1024 * 1024, // 15MB
            width = 3840,
            height = 2160,
            durationMs = 65000L, // 1m 5s
            dateAdded = 2000L,
            dateModified = 2000L,
            isFavorite = false
        ),
        MediaItem(
            id = 3,
            uri = "content://3",
            localPath = "/data/3.jpg",
            displayName = "cherry.jpg",
            mimeType = "image/jpeg",
            mediaType = MediaType.IMAGE,
            sizeBytes = 12 * 1024 * 1024, // 12MB
            width = 1080,
            height = 1080,
            dateAdded = 3000L,
            dateModified = 3000L,
            isFavorite = true
        )
    )

    @Test
    fun testFilterByImages() {
        val images = sampleItems.filter { it.mediaType == MediaType.IMAGE }
        assertEquals(2, images.size)
        assertTrue(images.all { it.mediaType == MediaType.IMAGE })
    }

    @Test
    fun testFilterByVideos() {
        val videos = sampleItems.filter { it.mediaType == MediaType.VIDEO }
        assertEquals(1, videos.size)
        assertEquals("apple.mp4", videos[0].displayName)
    }

    @Test
    fun testFilterByFavorites() {
        val favorites = sampleItems.filter { it.isFavorite }
        assertEquals(2, favorites.size)
        assertTrue(favorites.all { it.isFavorite })
    }

    @Test
    fun testFilterByLargeFiles() {
        val largeFiles = sampleItems.filter { it.sizeBytes >= 10 * 1024 * 1024 }
        assertEquals(2, largeFiles.size)
    }

    @Test
    fun testSortByNameAscending() {
        val sorted = sampleItems.sortedBy { it.displayName.lowercase() }
        assertEquals("apple.mp4", sorted[0].displayName)
        assertEquals("banana.jpg", sorted[1].displayName)
        assertEquals("cherry.jpg", sorted[2].displayName)
    }

    @Test
    fun testSortByDateDescending() {
        val sorted = sampleItems.sortedByDescending { it.dateAdded }
        assertEquals(3L, sorted[0].id)
        assertEquals(2L, sorted[1].id)
        assertEquals(1L, sorted[2].id)
    }

    @Test
    fun testSortBySizeDescending() {
        val sorted = sampleItems.sortedByDescending { it.sizeBytes }
        assertEquals("apple.mp4", sorted[0].displayName) // 15MB
        assertEquals("cherry.jpg", sorted[1].displayName) // 12MB
        assertEquals("banana.jpg", sorted[2].displayName) // 2MB
    }
}
