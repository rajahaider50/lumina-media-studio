package com.example.premiumapp.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val title: String,
    val description: String,
    val iconName: String
)

class OnboardingViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    val pages = listOf(
        OnboardingPageData(
            title = "Complete Offline Privacy",
            description = "All your media remains strictly local on your device. No cloud sync, zero telemetry, full control.",
            iconName = "Security"
        ),
        OnboardingPageData(
            title = "Studio-Grade Media Tools",
            description = "High-performance image transformations, video trimming, and metadata inspection built with native Android APIs.",
            iconName = "Tune"
        ),
        OnboardingPageData(
            title = "Curated Smart Collections",
            description = "Organize, tag favorites, and search your entire media library with instant offline speed.",
            iconName = "Collections"
        )
    )

    fun onNextPage() {
        if (_currentPage.value < pages.size - 1) {
            _currentPage.value += 1
        }
    }

    fun onPreviousPage() {
        if (_currentPage.value > 0) {
            _currentPage.value -= 1
        }
    }

    fun completeOnboarding(onCompleted: () -> Unit) {
        viewModelScope.launch {
            preferencesRepository.setOnboardingCompleted(true)
            onCompleted()
        }
    }
}
