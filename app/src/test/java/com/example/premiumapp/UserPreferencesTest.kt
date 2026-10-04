package com.example.premiumapp

import com.example.premiumapp.core.common.AppError
import com.example.premiumapp.core.common.AppResult
import com.example.premiumapp.domain.model.GridDensity
import com.example.premiumapp.domain.model.SortMode
import com.example.premiumapp.domain.model.ThemeMode
import com.example.premiumapp.domain.model.ThumbnailQuality
import com.example.premiumapp.domain.model.UserPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserPreferencesTest {

    @Test
    fun testDefaultUserPreferences() {
        val prefs = UserPreferences()
        assertFalse(prefs.onboardingCompleted)
        assertEquals(ThemeMode.SYSTEM, prefs.themeMode)
        assertTrue(prefs.dynamicColor)
        assertFalse(prefs.reduceMotion)
        assertEquals(GridDensity.STANDARD, prefs.gridDensity)
        assertEquals(SortMode.NEWEST, prefs.sortMode)
        assertEquals(ThumbnailQuality.HIGH, prefs.thumbnailQuality)
        assertTrue(prefs.notificationsEnabled)
        assertEquals("Lumina Creator", prefs.userName)
    }

    @Test
    fun testCustomUserPreferences() {
        val prefs = UserPreferences(
            onboardingCompleted = true,
            themeMode = ThemeMode.DARK,
            dynamicColor = false,
            reduceMotion = true,
            gridDensity = GridDensity.COMPACT,
            sortMode = SortMode.NAME_AZ,
            thumbnailQuality = ThumbnailQuality.MEDIUM,
            notificationsEnabled = false,
            userName = "Alice"
        )
        assertTrue(prefs.onboardingCompleted)
        assertEquals(ThemeMode.DARK, prefs.themeMode)
        assertFalse(prefs.dynamicColor)
        assertTrue(prefs.reduceMotion)
        assertEquals(GridDensity.COMPACT, prefs.gridDensity)
        assertEquals(SortMode.NAME_AZ, prefs.sortMode)
        assertEquals("Alice", prefs.userName)
    }

    @Test
    fun testAppResultSuccessAndError() {
        val success: AppResult<String> = AppResult.Success("test_data")
        assertTrue(success is AppResult.Success)
        assertEquals("test_data", (success as AppResult.Success).data)

        val error: AppResult<String> = AppResult.Error(AppError.MediaNotFound("File missing"))
        assertTrue(error is AppResult.Error)
        assertEquals("File missing", (error as AppResult.Error).error.message)
    }
}
