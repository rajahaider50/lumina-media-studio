package com.example.premiumapp.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.domain.model.ActivityItem
import com.example.premiumapp.domain.model.ActivityType
import com.example.premiumapp.domain.repository.ActivityRepository
import com.example.premiumapp.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotificationItem(
    val id: Long,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isAlert: Boolean = false
)

data class NotificationsCenterUiState(
    val notificationsEnabled: Boolean = true,
    val notificationHistory: List<NotificationItem> = emptyList()
)

class NotificationsCenterViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsCenterUiState())
    val uiState: StateFlow<NotificationsCenterUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            launch {
                preferencesRepository.getUserPreferences().collect { prefs ->
                    _uiState.value = _uiState.value.copy(notificationsEnabled = prefs.notificationsEnabled)
                }
            }

            launch {
                activityRepository.getRecentActivities(50).collect { activities ->
                    val notifications = activities.map { act ->
                        NotificationItem(
                            id = act.id,
                            title = when (act.type) {
                                ActivityType.IMPORTED -> "Import Complete"
                                ActivityType.EXPORTED -> "Export Ready"
                                ActivityType.EDITED -> "Edit Saved"
                                ActivityType.DELETED -> "Item Removed"
                                else -> "Library Update"
                            },
                            message = act.description,
                            timestamp = act.timestamp,
                            isAlert = act.type == ActivityType.DELETED
                        )
                    }
                    _uiState.value = _uiState.value.copy(notificationHistory = notifications)
                }
            }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotificationsEnabled(enabled)
        }
    }
}
