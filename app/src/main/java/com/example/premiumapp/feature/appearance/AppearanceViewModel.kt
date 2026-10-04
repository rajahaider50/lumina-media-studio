package com.example.premiumapp.feature.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.design.theme.ThemeMode
import com.example.premiumapp.domain.model.GridDensity
import com.example.premiumapp.domain.model.ThumbnailQuality
import com.example.premiumapp.domain.model.UserPreferences
import com.example.premiumapp.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppearanceViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _userPreferences = MutableStateFlow(UserPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.getUserPreferences().collect { prefs ->
                _userPreferences.value = prefs
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setDynamicColor(enabled)
        }
    }

    fun setReduceMotion(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setReduceMotion(enabled)
        }
    }

    fun setGridDensity(density: GridDensity) {
        viewModelScope.launch {
            preferencesRepository.setGridDensity(density)
        }
    }

    fun setThumbnailQuality(quality: ThumbnailQuality) {
        viewModelScope.launch {
            preferencesRepository.setThumbnailQuality(quality)
        }
    }
}
