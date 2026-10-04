package com.example.premiumapp.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.premiumapp.design.theme.ThemeMode
import com.example.premiumapp.domain.model.GridDensity
import com.example.premiumapp.domain.model.SortMode
import com.example.premiumapp.domain.model.ThumbnailQuality
import com.example.premiumapp.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class PreferencesManager(private val context: Context) {

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val REDUCE_MOTION = booleanPreferencesKey("reduce_motion")
        val GRID_DENSITY = stringPreferencesKey("grid_density")
        val SORT_MODE = stringPreferencesKey("sort_mode")
        val THUMBNAIL_QUALITY = stringPreferencesKey("thumbnail_quality")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val USER_NAME = stringPreferencesKey("user_name")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        val onboarding = preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        val themeModeStr = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        val themeMode = try { ThemeMode.valueOf(themeModeStr) } catch (e: Exception) { ThemeMode.SYSTEM }
        val dynamicColor = preferences[PreferencesKeys.DYNAMIC_COLOR] ?: false
        val reduceMotion = preferences[PreferencesKeys.REDUCE_MOTION] ?: false
        val gridDensityStr = preferences[PreferencesKeys.GRID_DENSITY] ?: GridDensity.STANDARD.name
        val gridDensity = try { GridDensity.valueOf(gridDensityStr) } catch (e: Exception) { GridDensity.STANDARD }
        val sortModeStr = preferences[PreferencesKeys.SORT_MODE] ?: SortMode.NEWEST.name
        val sortMode = try { SortMode.valueOf(sortModeStr) } catch (e: Exception) { SortMode.NEWEST }
        val thumbQualityStr = preferences[PreferencesKeys.THUMBNAIL_QUALITY] ?: ThumbnailQuality.HIGH.name
        val thumbQuality = try { ThumbnailQuality.valueOf(thumbQualityStr) } catch (e: Exception) { ThumbnailQuality.HIGH }
        val notifsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true

        UserPreferences(
            onboardingCompleted = onboarding,
            themeMode = themeMode,
            dynamicColor = dynamicColor,
            reduceMotion = reduceMotion,
            gridDensity = gridDensity,
            sortMode = sortMode,
            thumbnailQuality = thumbQuality,
            notificationsEnabled = notifsEnabled
        )
    }

    val userNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_NAME] ?: "Local Creator"
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DYNAMIC_COLOR] = enabled
        }
    }

    suspend fun setReduceMotion(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REDUCE_MOTION] = enabled
        }
    }

    suspend fun setGridDensity(density: GridDensity) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GRID_DENSITY] = density.name
        }
    }

    suspend fun setSortMode(mode: SortMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_MODE] = mode.name
        }
    }

    suspend fun setThumbnailQuality(quality: ThumbnailQuality) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THUMBNAIL_QUALITY] = quality.name
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }
}
