package com.example.premiumapp.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.domain.repository.CollectionRepository
import com.example.premiumapp.domain.repository.MediaRepository
import com.example.premiumapp.domain.repository.PreferencesRepository
import com.example.premiumapp.domain.usecase.GetStorageInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userName: String = "Local Creator",
    val mediaCount: Int = 0,
    val collectionCount: Int = 0,
    val favoritesCount: Int = 0,
    val storageUsedBytes: Long = 0L
)

class ProfileViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val mediaRepository: MediaRepository,
    private val collectionRepository: CollectionRepository,
    private val getStorageInfoUseCase: GetStorageInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            launch {
                preferencesRepository.getUserName().collect { name ->
                    _uiState.value = _uiState.value.copy(userName = name)
                }
            }
            launch {
                mediaRepository.getMediaCount().collect { count ->
                    _uiState.value = _uiState.value.copy(mediaCount = count)
                }
            }
            launch {
                collectionRepository.getCollectionsCount().collect { count ->
                    _uiState.value = _uiState.value.copy(collectionCount = count)
                }
            }
            launch {
                mediaRepository.getFavoritesCount().collect { count ->
                    _uiState.value = _uiState.value.copy(favoritesCount = count)
                }
            }
            launch {
                val stats = getStorageInfoUseCase()
                _uiState.value = _uiState.value.copy(storageUsedBytes = stats.mediaStorageBytes)
            }
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            preferencesRepository.setUserName(name)
        }
    }
}
