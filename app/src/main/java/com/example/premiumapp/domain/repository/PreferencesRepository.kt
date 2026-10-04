package com.example.premiumapp.domain.repository

import com.example.premiumapp.design.theme.ThemeMode
import com.example.premiumapp.domain.model.GridDensity
import com.example.premiumapp.domain.model.SortMode
import com.example.premiumapp.domain.model.ThumbnailQuality
import com.example.premiumapp.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getUserPreferences(): Flow<UserPreferences>
    fun getUserName(): Flow<String>
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setDynamicColor(enabled: Boolean)
    suspend fun setReduceMotion(enabled: Boolean)
    suspend fun setGridDensity(density: GridDensity)
    suspend fun setSortMode(mode: SortMode)
    suspend fun setThumbnailQuality(quality: ThumbnailQuality)
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setUserName(name: String)
}
