package com.example.premiumapp.feature.collections

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.premiumapp.core.extensions.formatDate
import com.example.premiumapp.design.components.EmptyState
import com.example.premiumapp.design.components.PremiumCard
import com.example.premiumapp.design.components.PremiumConfirmDialog
import com.example.premiumapp.design.components.PremiumIconButton
import com.example.premiumapp.design.components.PremiumInputDialog
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppRadius
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.AppShapes
import com.example.premiumapp.design.theme.PremiumTheme
import com.example.premiumapp.domain.model.CollectionItem

@Composable
fun CollectionsScreen(
    viewModel: CollectionsViewModel,
    onNavigateToCreateCollection: () -> Unit,
    onNavigateToCollectionDetails: (Long) -> Unit
) {
    val collections by viewModel.collections.collectAsState()

    var collectionToDelete by remember { mutableStateOf<CollectionItem?>(null) }
    var collectionToRename by remember { mutableStateOf<CollectionItem?>(null) }

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = "Collections",
                subtitle = "${collections.size} albums",
                actions = {
                    PremiumIconButton(
                        icon = AppIcons.Add,
                        contentDescription = "Create New Collection",
                        onClick = onNavigateToCreateCollection
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (collections.isEmpty()) {
                EmptyState(
                    title = "No Collections Yet",
                    description = "Create collections to organize your personal photos and videos into custom albums.",
                    icon = AppIcons.Folder,
                    actionText = "Create Collection",
                    onActionClick = onNavigateToCreateCollection
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(AppSpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
                ) {
                    items(collections, key = { it.id }) { collection ->
                        var menuExpanded by remember { mutableStateOf(false) }

                        PremiumCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToCollectionDetails(collection.id) },
                            shape = AppShapes.large,
                            backgroundColor = PremiumTheme.colors.surface,
                            contentPadding = AppSpacing.sm
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(AppRadius.medium))
                                    .background(PremiumTheme.colors.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                if (collection.coverUri != null) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(collection.coverUri)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = collection.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = AppIcons.Folder,
                                        contentDescription = null,
                                        tint = PremiumTheme.colors.primary,
                                        modifier = Modifier.size(AppIconSize.hero)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(AppSpacing.sm))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = collection.name,
                                        style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = PremiumTheme.colors.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${collection.mediaCount} items • ${collection.updatedAt.formatDate()}",
                                        style = PremiumTheme.typography.bodySmall,
                                        color = PremiumTheme.colors.textSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Box {
                                    PremiumIconButton(
                                        icon = AppIcons.More,
                                        contentDescription = "Collection Options",
                                        onClick = { menuExpanded = true },
                                        iconSize = AppIconSize.small
                                    )
                                    DropdownMenu(
                                        expanded = menuExpanded,
                                        onDismissRequest = { menuExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Rename") },
                                            onClick = {
                                                menuExpanded = false
                                                collectionToRename = collection
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Delete", color = PremiumTheme.colors.error) },
                                            onClick = {
                                                menuExpanded = false
                                                collectionToDelete = collection
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Delete Dialog
            collectionToDelete?.let { col ->
                PremiumConfirmDialog(
                    title = "Delete Collection?",
                    message = "Are you sure you want to delete \"${col.name}\"? Photos and videos inside this collection will NOT be deleted from your library.",
                    confirmText = "Delete",
                    isDestructive = true,
                    onConfirm = {
                        viewModel.deleteCollection(col.id)
                        collectionToDelete = null
                    },
                    onDismiss = { collectionToDelete = null }
                )
            }

            // Rename Dialog
            collectionToRename?.let { col ->
                PremiumInputDialog(
                    title = "Rename Collection",
                    initialValue = col.name,
                    label = "Collection Name",
                    onConfirm = { newName ->
                        viewModel.renameCollection(col.id, newName)
                        collectionToRename = null
                    },
                    onDismiss = { collectionToRename = null }
                )
            }
        }
    }
}
