package com.example.premiumapp.feature.collectiondetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.premiumapp.core.extensions.formatDate
import com.example.premiumapp.design.components.EmptyState
import com.example.premiumapp.design.components.MediaListItem
import com.example.premiumapp.design.components.MediaThumbnail
import com.example.premiumapp.design.components.PremiumButton
import com.example.premiumapp.design.components.PremiumCard
import com.example.premiumapp.design.components.PremiumConfirmDialog
import com.example.premiumapp.design.components.PremiumIconButton
import com.example.premiumapp.design.components.PremiumInputDialog
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.AppShapes
import com.example.premiumapp.design.theme.PremiumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailsScreen(
    viewModel: CollectionDetailsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMediaViewer: (Long) -> Unit,
    onNavigateToImport: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val collection = state.collection

    var showAddMediaSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = collection?.name ?: "Collection",
                subtitle = "${state.mediaItems.size} items",
                navigationIcon = AppIcons.Back,
                navigationIconContentDescription = "Go Back",
                onNavigationClick = onNavigateBack,
                actions = {
                    PremiumIconButton(
                        icon = AppIcons.Add,
                        contentDescription = "Add Media to Collection",
                        onClick = { showAddMediaSheet = true }
                    )
                    Box {
                        PremiumIconButton(
                            icon = AppIcons.More,
                            contentDescription = "Collection Options",
                            onClick = { menuExpanded = true }
                        )
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Rename") },
                                onClick = {
                                    menuExpanded = false
                                    showRenameDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Collection", color = PremiumTheme.colors.error) },
                                onClick = {
                                    menuExpanded = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Optional Description Card
            if (!collection?.description.isNullOrBlank()) {
                PremiumCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.md, vertical = AppSpacing.xs),
                    shape = AppShapes.medium,
                    backgroundColor = PremiumTheme.colors.surface
                ) {
                    Text(
                        text = collection!!.description!!,
                        style = PremiumTheme.typography.bodyMedium,
                        color = PremiumTheme.colors.textSecondary
                    )
                }
            }

            if (state.mediaItems.isEmpty()) {
                EmptyState(
                    title = "This Collection is Empty",
                    description = "Add photos and videos from your existing library or import new ones.",
                    icon = AppIcons.CollectionsFilled,
                    actionText = "Add Media",
                    onActionClick = { showAddMediaSheet = true }
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(AppSpacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                ) {
                    items(state.mediaItems, key = { it.id }) { item ->
                        MediaThumbnail(
                            item = item,
                            onClick = { onNavigateToMediaViewer(item.id) }
                        )
                    }
                }
            }
        }

        // Add Media Bottom Sheet
        if (showAddMediaSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddMediaSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = PremiumTheme.colors.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.lg)
                ) {
                    Text(
                        text = "Add Media to Collection",
                        style = PremiumTheme.typography.titleLarge,
                        color = PremiumTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.md))

                    val availableToAdd = state.allLibraryMedia.filter { item ->
                        state.mediaItems.none { it.id == item.id }
                    }

                    if (availableToAdd.isEmpty()) {
                        EmptyState(
                            title = "No More Media Available",
                            description = "All library items are already added to this collection.",
                            icon = AppIcons.ImagePlaceholder,
                            actionText = "Import New Media",
                            onActionClick = {
                                showAddMediaSheet = false
                                onNavigateToImport()
                            }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp)
                        ) {
                            items(availableToAdd, key = { it.id }) { item ->
                                MediaListItem(
                                    item = item,
                                    onClick = {
                                        viewModel.addMediaToCollection(item.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Delete Dialog
        if (showDeleteDialog && collection != null) {
            PremiumConfirmDialog(
                title = "Delete Collection?",
                message = "Are you sure you want to delete \"${collection.name}\"? Photos and videos will remain in your library.",
                confirmText = "Delete",
                isDestructive = true,
                onConfirm = {
                    viewModel.deleteCollection(onNavigateBack)
                },
                onDismiss = { showDeleteDialog = false }
            )
        }

        // Rename Dialog
        if (showRenameDialog && collection != null) {
            PremiumInputDialog(
                title = "Rename Collection",
                initialValue = collection.name,
                label = "Collection Name",
                onConfirm = { newName ->
                    viewModel.renameCollection(newName)
                    showRenameDialog = false
                },
                onDismiss = { showRenameDialog = false }
            )
        }
    }
}
