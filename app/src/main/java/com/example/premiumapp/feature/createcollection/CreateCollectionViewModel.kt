package com.example.premiumapp.feature.createcollection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.domain.usecase.CreateCollectionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreateCollectionUiState(
    val name: String = "",
    val description: String = "",
    val nameError: String? = null,
    val isCreating: Boolean = false,
    val createdCollectionId: Long? = null
)

class CreateCollectionViewModel(
    private val createCollectionUseCase: CreateCollectionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateCollectionUiState())
    val uiState: StateFlow<CreateCollectionUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(
            name = name,
            nameError = if (name.isNotBlank()) null else _uiState.value.nameError
        )
    }

    fun onDescriptionChange(desc: String) {
        _uiState.value = _uiState.value.copy(description = desc)
    }

    fun createCollection(onSuccess: (Long) -> Unit) {
        val name = _uiState.value.name.trim()
        if (name.isEmpty()) {
            _uiState.value = _uiState.value.copy(nameError = "Collection name cannot be empty")
            return
        }
        if (name.length > 50) {
            _uiState.value = _uiState.value.copy(nameError = "Name cannot exceed 50 characters")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true)
            val newId = createCollectionUseCase(
                name = name,
                description = _uiState.value.description.trim().ifEmpty { null }
            )
            _uiState.value = _uiState.value.copy(isCreating = false, createdCollectionId = newId)
            onSuccess(newId)
        }
    }
}
