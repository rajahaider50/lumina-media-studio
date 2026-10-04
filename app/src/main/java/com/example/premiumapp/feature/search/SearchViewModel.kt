package com.example.premiumapp.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.repository.MediaRepository
import com.example.premiumapp.domain.repository.RecentSearchRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val results: List<MediaItem> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val isSearching: Boolean = false
)

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val mediaRepository: MediaRepository,
    private val recentSearchRepository: RecentSearchRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _searchResults = MutableStateFlow<List<MediaItem>>(emptyList())
    val searchResults: StateFlow<List<MediaItem>> = _searchResults.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    init {
        // Load recent searches
        viewModelScope.launch {
            recentSearchRepository.getRecentSearches().collect { searches ->
                _recentSearches.value = searches
            }
        }

        // Debounced search query listener
        viewModelScope.launch {
            _query
                .debounce(250)
                .distinctUntilChanged()
                .flatMapLatest { q ->
                    if (q.isBlank()) {
                        flowOf(emptyList())
                    } else {
                        mediaRepository.searchMedia(q.trim())
                    }
                }
                .collect { results ->
                    _searchResults.value = results
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun executeSearch(searchQuery: String) {
        _query.value = searchQuery
        if (searchQuery.isNotBlank()) {
            viewModelScope.launch {
                recentSearchRepository.addSearch(searchQuery.trim())
            }
        }
    }

    fun removeRecentSearch(searchQuery: String) {
        viewModelScope.launch {
            recentSearchRepository.removeSearch(searchQuery)
        }
    }

    fun clearAllRecentSearches() {
        viewModelScope.launch {
            recentSearchRepository.clearSearches()
        }
    }
}
