package com.example.premiumapp.feature.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.premiumapp.design.components.EmptyState
import com.example.premiumapp.design.components.MediaListItem
import com.example.premiumapp.design.components.MediaThumbnail
import com.example.premiumapp.design.components.PremiumIconButton
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.PremiumTheme

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onNavigateToMediaViewer: (Long) -> Unit,
    onNavigateToExplore: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = "Favorites",
                subtitle = "${state.favorites.size} starred items",
                actions = {
                    PremiumIconButton(
                        icon = if (state.isListView) Icons.Filled.GridView else Icons.Filled.ViewList,
                        contentDescription = "Toggle Grid/List",
                        onClick = { viewModel.toggleLayout() }
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
            if (state.favorites.isEmpty()) {
                EmptyState(
                    title = "No Favorites Yet",
                    description = "Star photos and videos to quickly access them in your favorites list.",
                    icon = AppIcons.FavoritesFilled,
                    actionText = "Explore Media",
                    onActionClick = onNavigateToExplore
                )
            } else if (state.isListView) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = AppSpacing.huge)
                ) {
                    items(state.favorites, key = { it.id }) { item ->
                        MediaListItem(
                            item = item,
                            onClick = { onNavigateToMediaViewer(item.id) },
                            onFavoriteToggle = { viewModel.removeFavorite(item.id) }
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(AppSpacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                ) {
                    items(state.favorites, key = { it.id }) { item ->
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
