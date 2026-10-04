package com.example.premiumapp.feature.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.domain.model.ActivityItem
import com.example.premiumapp.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecentActivityViewModel(
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val _activities = MutableStateFlow<List<ActivityItem>>(emptyList())
    val activities: StateFlow<List<ActivityItem>> = _activities.asStateFlow()

    init {
        loadActivities()
    }

    private fun loadActivities() {
        viewModelScope.launch {
            activityRepository.getRecentActivities(100).collect { list ->
                _activities.value = list
            }
        }
    }

    fun clearAllActivities() {
        viewModelScope.launch {
            activityRepository.clearActivities()
        }
    }
}
