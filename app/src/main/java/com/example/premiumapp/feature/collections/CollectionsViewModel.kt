package com.example.premiumapp.feature.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.domain.model.CollectionItem
import com.example.premiumapp.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectionsViewModel(
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _collections = MutableStateFlow<List<CollectionItem>>(emptyList())
    val collections: StateFlow<List<CollectionItem>> = _collections.asStateFlow()

    init {
        loadCollections()
    }

    private fun loadCollections() {
        viewModelScope.launch {
            collectionRepository.getAllCollections().collect { list ->
                _collections.value = list
            }
        }
    }

    fun renameCollection(id: Long, newName: String) {
        viewModelScope.launch {
            val existing = collectionRepository.getCollectionByIdDirect(id) ?: return@launch
            collectionRepository.updateCollection(existing.copy(name = newName))
        }
    }

    fun deleteCollection(id: Long) {
        viewModelScope.launch {
            collectionRepository.deleteCollection(id)
        }
    }
}
