package com.example.premiumapp.feature.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.domain.model.GridDensity
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.model.MediaType
import com.example.premiumapp.domain.model.SortMode
import com.example.premiumapp.domain.repository.MediaRepository
import com.example.premiumapp.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

enum class ExploreFilter(val label: String) {
    ALL("All"),
    IMAGES("Images"),
    VIDEOS("Videos"),
    FAVORITES("Favorites"),
    LARGE_FILES("Large Files")
}

data class ExploreUiState(
    val mediaItems: List<MediaItem> = emptyList(),
    val selectedFilter: ExploreFilter = ExploreFilter.ALL,
    val selectedSortMode: SortMode = SortMode.NEWEST,
    val isListView: Boolean = false,
    val gridDensity: GridDensity = GridDensity.STANDARD,
    val isLoading: Boolean = false
)

class ExploreViewModel(
    private val mediaRepository: MediaRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState(isLoading = true))
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                mediaRepository.getAllMedia(),
                preferencesRepository.getUserPreferences()
            ) { allMedia, prefs ->
                val filtered = applyFilterAndSort(allMedia, _uiState.value.selectedFilter, _uiState.value.selectedSortMode)
                _uiState.value.copy(
                    mediaItems = filtered,
                    gridDensity = prefs.gridDensity,
                    isLoading = false
                )
            }.collect { updatedState ->
                _uiState.value = updatedState
            }
        }
    }

    fun setFilter(filter: ExploreFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        refreshList()
    }

    fun setSortMode(sortMode: SortMode) {
        _uiState.value = _uiState.value.copy(selectedSortMode = sortMode)
        refreshList()
    }

    fun toggleLayoutMode() {
        _uiState.value = _uiState.value.copy(isListView = !_uiState.value.isListView)
    }

    fun toggleFavorite(id: Long, currentFavorite: Boolean) {
        viewModelScope.launch {
            mediaRepository.toggleFavorite(id, !currentFavorite)
        }
    }

    private fun refreshList() {
        viewModelScope.launch {
            mediaRepository.getAllMedia().collect { allMedia ->
                val filtered = applyFilterAndSort(allMedia, _uiState.value.selectedFilter, _uiState.value.selectedSortMode)
                _uiState.value = _uiState.value.copy(mediaItems = filtered)
            }
        }
    }

    private fun applyFilterAndSort(
        items: List<MediaItem>,
        filter: ExploreFilter,
        sort: SortMode
    ): List<MediaItem> {
        val filtered = when (filter) {
            ExploreFilter.ALL -> items
            ExploreFilter.IMAGES -> items.filter { it.isImage }
            ExploreFilter.VIDEOS -> items.filter { it.isVideo }
            ExploreFilter.FAVORITES -> items.filter { it.isFavorite }
            ExploreFilter.LARGE_FILES -> items.filter { it.sizeBytes > 10 * 1024 * 1024L }
        }

        return when (sort) {
            SortMode.NEWEST -> filtered.sortedByDescending { it.dateAdded }
            SortMode.OLDEST -> filtered.sortedBy { it.dateAdded }
            SortMode.NAME_AZ -> filtered.sortedBy { it.displayName.lowercase() }
            SortMode.NAME_ZA -> filtered.sortedByDescending { it.displayName.lowercase() }
            SortMode.LARGEST -> filtered.sortedByDescending { it.sizeBytes }
            SortMode.SMALLEST -> filtered.sortedBy { it.sizeBytes }
        }
    }
}
