package com.example.premiumapp.feature.explore

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.premiumapp.design.components.EmptyState
import com.example.premiumapp.design.components.FilterChipRow
import com.example.premiumapp.design.components.MediaListItem
import com.example.premiumapp.design.components.MediaThumbnail
import com.example.premiumapp.design.components.PremiumIconButton
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.PremiumTheme
import com.example.premiumapp.domain.model.GridDensity
import com.example.premiumapp.domain.model.SortMode

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onNavigateToMediaViewer: (Long) -> Unit,
    onNavigateToSelection: () -> Unit,
    onNavigateToImport: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = "Explore",
                subtitle = "${state.mediaItems.size} items",
                actions = {
                    PremiumIconButton(
                        icon = if (state.isListView) Icons.Filled.GridView else Icons.Filled.ViewList,
                        contentDescription = "Toggle Grid/List Layout",
                        onClick = { viewModel.toggleLayoutMode() }
                    )
                    Box {
                        PremiumIconButton(
                            icon = AppIcons.Sort,
                            contentDescription = "Sort Options",
                            onClick = { sortMenuExpanded = true }
                        )
                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false }
                        ) {
                            SortMode.values().forEach { mode ->
                                DropdownMenuItem(
                                    text = { Text(mode.name.replace("_", " ")) },
                                    onClick = {
                                        viewModel.setSortMode(mode)
                                        sortMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    PremiumIconButton(
                        icon = AppIcons.SelectAll,
                        contentDescription = "Selection Mode",
                        onClick = onNavigateToSelection
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            FilterChipRow(
                filters = ExploreFilter.values().map { it.label },
                selectedFilter = state.selectedFilter.label,
                onFilterSelected = { label ->
                    val filter = ExploreFilter.values().firstOrNull { it.label == label } ?: ExploreFilter.ALL
                    viewModel.setFilter(filter)
                }
            )

            if (state.mediaItems.isEmpty()) {
                EmptyState(
                    title = "No Media in this Category",
                    description = "Try selecting a different filter or import new photos and videos.",
                    icon = AppIcons.ImagePlaceholder,
                    actionText = "Import Media",
                    onActionClick = onNavigateToImport
                )
            } else if (state.isListView) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = AppSpacing.huge)
                ) {
                    items(state.mediaItems, key = { it.id }) { item ->
                        MediaListItem(
                            item = item,
                            onClick = { onNavigateToMediaViewer(item.id) },
                            onFavoriteToggle = { viewModel.toggleFavorite(item.id, item.isFavorite) }
                        )
                    }
                }
            } else {
                val columns = when (state.gridDensity) {
                    GridDensity.COMPACT -> 4
                    GridDensity.STANDARD -> 3
                    GridDensity.LARGE -> 2
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(AppSpacing.sm),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(AppSpacing.xs),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(AppSpacing.xs)
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
    }
}
