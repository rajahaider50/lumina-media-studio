package com.example.premiumapp.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.domain.model.UserPreferences
import com.example.premiumapp.domain.repository.ActivityRepository
import com.example.premiumapp.domain.repository.PreferencesRepository
import com.example.premiumapp.domain.repository.RecentSearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val activityRepository: ActivityRepository,
    private val recentSearchRepository: RecentSearchRepository
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

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotificationsEnabled(enabled)
        }
    }

    fun setReduceMotion(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setReduceMotion(enabled)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            recentSearchRepository.clearSearches()
        }
    }

    fun clearActivityHistory() {
        viewModelScope.launch {
            activityRepository.clearActivities()
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            preferencesRepository.setOnboardingCompleted(false)
        }
    }
}
