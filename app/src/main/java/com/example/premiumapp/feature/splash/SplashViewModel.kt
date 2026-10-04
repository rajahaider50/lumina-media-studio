package com.example.premiumapp.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.domain.repository.PreferencesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface SplashNavigationTarget {
    data object Loading : SplashNavigationTarget
    data object Onboarding : SplashNavigationTarget
    data object Home : SplashNavigationTarget
}

class SplashViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _navigationTarget = MutableStateFlow<SplashNavigationTarget>(SplashNavigationTarget.Loading)
    val navigationTarget: StateFlow<SplashNavigationTarget> = _navigationTarget.asStateFlow()

    init {
        checkInitialDestination()
    }

    private fun checkInitialDestination() {
        viewModelScope.launch {
            // Smooth natural entrance delay
            delay(800)
            val preferences = preferencesRepository.getUserPreferences().first()
            if (preferences.onboardingCompleted) {
                _navigationTarget.value = SplashNavigationTarget.Home
            } else {
                _navigationTarget.value = SplashNavigationTarget.Onboarding
            }
        }
    }
}
