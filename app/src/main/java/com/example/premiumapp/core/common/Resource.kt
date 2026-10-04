package com.example.premiumapp.core.common

sealed interface Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>
    data class Error(val exception: Throwable, val message: String = exception.localizedMessage ?: "Unknown error") : Resource<Nothing>
    data object Loading : Resource<Nothing>
    data object Empty : Resource<Nothing>
}

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data object Empty : UiState<Nothing>
    data class Error(val message: String, val recoveryAction: (() -> Unit)? = null) : UiState<Nothing>
}

sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
    data class Navigate(val route: String) : UiEvent
    data object NavigateUp : UiEvent
}
