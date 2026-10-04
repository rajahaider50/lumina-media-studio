package com.example.premiumapp.feature.storage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.core.navigation.Screen
import com.example.premiumapp.design.components.LuminaCard
import com.example.premiumapp.design.components.LuminaDialog
import com.example.premiumapp.design.components.LuminaIconButton
import com.example.premiumapp.design.components.LuminaPrimaryButton
import com.example.premiumapp.design.components.LuminaTopBar
import com.example.premiumapp.design.components.MediaListItem
import com.example.premiumapp.design.components.StorageBar
import com.example.premiumapp.design.dimensions.LuminaIconSize
import com.example.premiumapp.design.dimensions.LuminaRadius
import com.example.premiumapp.design.dimensions.LuminaSpacing
import com.example.premiumapp.design.theme.LuminaCyan
import com.example.premiumapp.design.theme.LuminaIndigo
import com.example.premiumapp.design.theme.LuminaThemeTokens
import com.example.premiumapp.design.theme.StatusWarning
import com.example.premiumapp.domain.model.FilterOption
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.model.SortOption
import com.example.premiumapp.domain.model.StorageInfo
import com.example.premiumapp.domain.repository.MediaRepository
import com.example.premiumapp.domain.usecase.DeleteMediaUseCase
import com.example.premiumapp.domain.usecase.GetStorageInfoUseCase
import com.example.premiumapp.domain.usecase.ToggleFavoriteUseCase
import com.example.premiumapp.design.components.SectionHeader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StorageUiState(
    val storageInfo: StorageInfo? = null,
    val largeFiles: List<MediaItem> = emptyList(),
    val isClearingCache: Boolean = false,
    val isLoading: Boolean = true
)

class StorageViewModel(
    private val getStorageInfoUseCase: GetStorageInfoUseCase,
    private val mediaRepository: MediaRepository,
    private val deleteMediaUseCase: DeleteMediaUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StorageUiState())
    val uiState: StateFlow<StorageUiState> = _uiState.asStateFlow()

    init {
        refreshStorage()
        loadLargeFiles()
    }

    fun refreshStorage() {
        viewModelScope.launch {
            val stats = getStorageInfoUseCase()
            val info = stats.toStorageInfo()
            _uiState.value = _uiState.value.copy(storageInfo = info, isLoading = false)
        }
    }

    private fun loadLargeFiles() {
        viewModelScope.launch {
            mediaRepository.getAllMedia().collect { allMedia ->
                val files = allMedia
                    .filter { it.sizeBytes >= 10 * 1024 * 1024 }
                    .sortedByDescending { it.sizeBytes }
                _uiState.value = _uiState.value.copy(largeFiles = files)
            }
        }
    }

    fun clearCache(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isClearingCache = true)
            val success = getStorageInfoUseCase.clearCache()
            refreshStorage()
            _uiState.value = _uiState.value.copy(isClearingCache = false)
            onComplete(success)
        }
    }

    fun deleteMedia(item: MediaItem) {
        viewModelScope.launch {
            deleteMediaUseCase(item.id)
            refreshStorage()
        }
    }

    fun toggleFavorite(item: MediaItem) {
        viewModelScope.launch {
            toggleFavoriteUseCase(item.id, !item.isFavorite)
        }
    }
}

@Composable
fun StorageScreen(
    viewModel: StorageViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMedia: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val extended = LuminaThemeTokens.extended
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showClearCacheDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<MediaItem?>(null) }

    Scaffold(
        topBar = {
            LuminaTopBar(
                title = "Storage Center",
                onBackClick = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(LuminaSpacing.large)
        ) {
            val storage = uiState.storageInfo

            // Storage Overview Card
            if (storage != null) {
                LuminaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Device Storage",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${storage.formattedFreeSpace} free of ${storage.formattedTotalSpace}",
                                style = MaterialTheme.typography.bodySmall,
                                color = extended.textSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(LuminaSpacing.medium))

                        StorageBar(
                            usedRatio = storage.deviceUsedPercent,
                            appMediaRatio = if (storage.totalDeviceBytes > 0) storage.appMediaBytes.toFloat() / storage.totalDeviceBytes else 0f,
                            appCacheRatio = if (storage.totalDeviceBytes > 0) storage.appCacheBytes.toFloat() / storage.totalDeviceBytes else 0f
                        )

                        Spacer(modifier = Modifier.height(LuminaSpacing.medium))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            StorageLegendItem(color = MaterialTheme.colorScheme.primary, label = "Studio Media", size = storage.formattedAppMedia)
                            StorageLegendItem(color = LuminaCyan, label = "Cache & Thumbs", size = storage.formattedAppCache)
                            StorageLegendItem(color = extended.textTertiary, label = "Other Apps", size = "")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(LuminaSpacing.large))

            // Cache Cleaning Card
            LuminaCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(LuminaCyan.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CleaningServices,
                            contentDescription = null,
                            tint = LuminaCyan,
                            modifier = Modifier.size(LuminaIconSize.standard)
                        )
                    }

                    Spacer(modifier = Modifier.width(LuminaSpacing.medium))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Cache Management",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Disposable cached thumbnails (${storage?.formattedAppCache ?: "0 B"}). Your original media remains safe.",
                            style = MaterialTheme.typography.bodySmall,
                            color = extended.textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(LuminaSpacing.small))

                    LuminaPrimaryButton(
                        text = if (uiState.isClearingCache) "Cleaning..." else "Clean",
                        enabled = !uiState.isClearingCache,
                        onClick = { showClearCacheDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(LuminaSpacing.large))

            // Large Files Section (>10MB)
            SectionHeader(title = "Large Files (${uiState.largeFiles.size})")

            if (uiState.largeFiles.isEmpty()) {
                Text(
                    text = "No files exceed 10 MB in your library.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = extended.textSecondary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(LuminaSpacing.small)) {
                    uiState.largeFiles.forEach { file ->
                        MediaListItem(
                            item = file,
                            onClick = { onNavigateToMedia(file.id) },
                            onFavoriteToggle = { viewModel.toggleFavorite(file) }
                        )
                    }
                }
            }
        }
    }

    // Clear Cache Dialog
    if (showClearCacheDialog) {
        LuminaDialog(
            title = "Clean Application Cache?",
            message = "This will delete cached thumbnails and temporary files to free up space. Thumbnails will regenerate when viewed. Your photos and videos will remain completely safe.",
            confirmText = "Clean Cache",
            onConfirm = {
                showClearCacheDialog = false
                viewModel.clearCache { success ->
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (success) "Cache cleaned successfully" else "Failed to clean some cache items"
                        )
                    }
                }
            },
            onDismiss = { showClearCacheDialog = false }
        )
    }

    // Delete Media Dialog
    itemToDelete?.let { item ->
        LuminaDialog(
            title = "Delete File?",
            message = "Permanently remove ${item.displayName} from your device?",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                viewModel.deleteMedia(item)
                itemToDelete = null
            },
            onDismiss = { itemToDelete = null }
        )
    }
}

@Composable
fun StorageLegendItem(color: Color, label: String, size: String) {
    val extended = LuminaThemeTokens.extended
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (size.isNotEmpty()) "$label ($size)" else label,
            style = MaterialTheme.typography.bodySmall,
            color = extended.textSecondary
        )
    }
}
