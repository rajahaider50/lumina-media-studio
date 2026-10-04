package com.example.premiumapp.feature.selection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.premiumapp.App
import com.example.premiumapp.design.components.ButtonVariant
import com.example.premiumapp.design.components.EmptyState
import com.example.premiumapp.design.components.MediaThumbnail
import com.example.premiumapp.design.components.PremiumButton
import com.example.premiumapp.design.components.PremiumCard
import com.example.premiumapp.design.components.PremiumConfirmDialog
import com.example.premiumapp.design.components.PremiumIconButton
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.AppShapes
import com.example.premiumapp.design.theme.PremiumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionScreen(
    viewModel: SelectionViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val selectedCount = state.selectedIds.size
    val allSelected = selectedCount == state.allMedia.size && state.allMedia.isNotEmpty()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddToCollectionSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = if (selectedCount == 0) "Select Items" else "$selectedCount Selected",
                navigationIcon = AppIcons.Close,
                navigationIconContentDescription = "Exit Selection Mode",
                onNavigationClick = onNavigateBack,
                actions = {
                    PremiumButton(
                        text = if (allSelected) "Deselect All" else "Select All",
                        onClick = {
                            if (allSelected) viewModel.clearSelection() else viewModel.selectAll()
                        },
                        variant = ButtonVariant.TEXT
                    )
                }
            )
        },
        bottomBar = {
            if (selectedCount > 0) {
                PremiumCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.md),
                    shape = AppShapes.extraLarge,
                    backgroundColor = PremiumTheme.colors.surface,
                    elevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PremiumIconButton(
                            icon = AppIcons.FavoritesFilled,
                            contentDescription = "Favorite Selected",
                            onClick = { viewModel.batchFavorite() },
                            tint = PremiumTheme.colors.error
                        )
                        PremiumIconButton(
                            icon = AppIcons.Folder,
                            contentDescription = "Add Selected to Collection",
                            onClick = { showAddToCollectionSheet = true }
                        )
                        PremiumIconButton(
                            icon = AppIcons.Share,
                            contentDescription = "Share Selected",
                            onClick = {
                                val selectedItems = state.allMedia.filter { state.selectedIds.contains(it.id) }
                                val exporter = App.getContainer(context).mediaExporter
                                val shareIntent = exporter.createShareMultipleIntent(selectedItems)
                                if (shareIntent != null) {
                                    context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Selected Media"))
                                }
                            }
                        )
                        PremiumIconButton(
                            icon = AppIcons.Delete,
                            contentDescription = "Delete Selected",
                            onClick = { showDeleteDialog = true },
                            tint = PremiumTheme.colors.error
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.allMedia.isEmpty()) {
                EmptyState(
                    title = "Library is Empty",
                    description = "Import photos and videos to use selection mode.",
                    icon = AppIcons.SelectAll
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(AppSpacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                ) {
                    items(state.allMedia, key = { it.id }) { item ->
                        val isSelected = state.selectedIds.contains(item.id)
                        MediaThumbnail(
                            item = item,
                            onClick = { viewModel.toggleItem(item.id) },
                            isSelected = isSelected,
                            isSelectionMode = true
                        )
                    }
                }
            }

            // Batch Add to Collection Sheet
            if (showAddToCollectionSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showAddToCollectionSheet = false },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    containerColor = PremiumTheme.colors.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppSpacing.lg)
                    ) {
                        Text(
                            text = "Add $selectedCount items to Collection",
                            style = PremiumTheme.typography.titleLarge,
                            color = PremiumTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.md))

                        if (state.availableCollections.isEmpty()) {
                            Text(
                                text = "No collections created yet. Create a collection first in the Collections tab.",
                                style = PremiumTheme.typography.bodyMedium,
                                color = PremiumTheme.colors.textSecondary
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                            ) {
                                items(state.availableCollections, key = { it.id }) { col ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.batchAddToCollection(col.id)
                                                showAddToCollectionSheet = false
                                            }
                                            .padding(vertical = AppSpacing.sm),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        androidx.compose.material3.Icon(
                                            imageVector = AppIcons.Folder,
                                            contentDescription = null,
                                            tint = PremiumTheme.colors.primary,
                                            modifier = Modifier.size(AppIconSize.standard)
                                        )
                                        Spacer(modifier = Modifier.size(AppSpacing.md))
                                        Column {
                                            Text(
                                                text = col.name,
                                                style = PremiumTheme.typography.titleMedium,
                                                color = PremiumTheme.colors.textPrimary
                                            )
                                            Text(
                                                text = "${col.mediaCount} items",
                                                style = PremiumTheme.typography.bodySmall,
                                                color = PremiumTheme.colors.textSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Batch Delete Dialog
            if (showDeleteDialog) {
                PremiumConfirmDialog(
                    title = "Delete $selectedCount Items?",
                    message = "These $selectedCount items will be permanently removed from your library. This cannot be undone.",
                    confirmText = "Delete All",
                    isDestructive = true,
                    onConfirm = {
                        viewModel.batchDelete(onNavigateBack)
                        showDeleteDialog = false
                    },
                    onDismiss = { showDeleteDialog = false }
                )
            }
        }
    }
}
