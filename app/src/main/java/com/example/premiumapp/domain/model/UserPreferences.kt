package com.example.premiumapp.domain.model

data class UserPreferences(
    val onboardingCompleted: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val reduceMotion: Boolean = false,
    val gridDensity: GridDensity = GridDensity.STANDARD,
    val sortMode: SortMode = SortMode.NEWEST,
    val thumbnailQuality: ThumbnailQuality = ThumbnailQuality.HIGH,
    val notificationsEnabled: Boolean = true,
    val userName: String = "Lumina Creator",
    val userAvatarUri: String? = null,
    val notifyOnImport: Boolean = true,
    val notifyOnExport: Boolean = true,
    val notifyOnStorageWarning: Boolean = true
)
