package com.example.premiumapp.data.repository

import com.example.premiumapp.data.datastore.PreferencesManager
import com.example.premiumapp.design.theme.ThemeMode
import com.example.premiumapp.domain.model.GridDensity
import com.example.premiumapp.domain.model.SortMode
import com.example.premiumapp.domain.model.ThumbnailQuality
import com.example.premiumapp.domain.model.UserPreferences
import com.example.premiumapp.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow

class PreferencesRepositoryImpl(
    private val preferencesManager: PreferencesManager
) : PreferencesRepository {

    override fun getUserPreferences(): Flow<UserPreferences> = preferencesManager.userPreferencesFlow
    override fun getUserName(): Flow<String> = preferencesManager.userNameFlow

    override suspend fun setOnboardingCompleted(completed: Boolean) =
        preferencesManager.setOnboardingCompleted(completed)

    override suspend fun setThemeMode(mode: ThemeMode) =
        preferencesManager.setThemeMode(mode)

    override suspend fun setDynamicColor(enabled: Boolean) =
        preferencesManager.setDynamicColor(enabled)

    override suspend fun setReduceMotion(enabled: Boolean) =
        preferencesManager.setReduceMotion(enabled)

    override suspend fun setGridDensity(density: GridDensity) =
        preferencesManager.setGridDensity(density)

    override suspend fun setSortMode(mode: SortMode) =
        preferencesManager.setSortMode(mode)

    override suspend fun setThumbnailQuality(quality: ThumbnailQuality) =
        preferencesManager.setThumbnailQuality(quality)

    override suspend fun setNotificationsEnabled(enabled: Boolean) =
        preferencesManager.setNotificationsEnabled(enabled)

    override suspend fun setUserName(name: String) =
        preferencesManager.setUserName(name)
}
