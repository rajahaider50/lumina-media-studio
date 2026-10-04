package com.example.premiumapp.feature.search

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.premiumapp.design.components.EmptyState
import com.example.premiumapp.design.components.MediaThumbnail
import com.example.premiumapp.design.components.PremiumIconButton
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.components.SectionHeader
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.AppShapes
import com.example.premiumapp.design.theme.PremiumTheme

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMediaViewer: (Long) -> Unit
) {
    val query by viewModel.query.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = "Search",
                navigationIcon = AppIcons.Back,
                navigationIconContentDescription = "Go Back",
                onNavigationClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Field
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onQueryChange(it) },
                placeholder = { Text("Search by file or item name...") },
                leadingIcon = {
                    Icon(
                        imageVector = AppIcons.Search,
                        contentDescription = "Search",
                        tint = PremiumTheme.colors.textSecondary
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        PremiumIconButton(
                            icon = AppIcons.Close,
                            contentDescription = "Clear search text",
                            onClick = { viewModel.onQueryChange("") },
                            iconSize = AppIconSize.small
                        )
                    }
                },
                singleLine = true,
                shape = AppShapes.large,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = PremiumTheme.colors.surface,
                    unfocusedContainerColor = PremiumTheme.colors.surface,
                    focusedBorderColor = PremiumTheme.colors.primary,
                    unfocusedBorderColor = PremiumTheme.colors.border,
                    focusedTextColor = PremiumTheme.colors.textPrimary,
                    unfocusedTextColor = PremiumTheme.colors.textPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.md, vertical = AppSpacing.xs)
            )

            if (query.isBlank()) {
                // Show Recent Searches
                if (recentSearches.isNotEmpty()) {
                    SectionHeader(
                        title = "Recent Searches",
                        actionText = "Clear All",
                        onActionClick = { viewModel.clearAllRecentSearches() }
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = AppSpacing.md)
                    ) {
                        items(recentSearches) { term ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.executeSearch(term) }
                                    .padding(vertical = AppSpacing.sm),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.History,
                                        contentDescription = null,
                                        tint = PremiumTheme.colors.textTertiary,
                                        modifier = Modifier.size(AppIconSize.standard)
                                    )
                                    Spacer(modifier = Modifier.size(AppSpacing.md))
                                    Text(
                                        text = term,
                                        style = PremiumTheme.typography.bodyLarge,
                                        color = PremiumTheme.colors.textPrimary
                                    )
                                }
                                PremiumIconButton(
                                    icon = AppIcons.Close,
                                    contentDescription = "Remove search term",
                                    onClick = { viewModel.removeRecentSearch(term) },
                                    iconSize = AppIconSize.small
                                )
                            }
                        }
                    }
                } else {
                    EmptyState(
                        title = "Search Your Media",
                        description = "Type keywords above to find photos and videos instantly.",
                        icon = AppIcons.Search,
                        modifier = Modifier.padding(top = AppSpacing.xl)
                    )
                }
            } else {
                // Show Results
                if (searchResults.isEmpty()) {
                    EmptyState(
                        title = "No Results Found",
                        description = "No media matched \"$query\". Try a different search keyword.",
                        icon = AppIcons.Search,
                        actionText = "Clear Search",
                        onActionClick = { viewModel.onQueryChange("") },
                        modifier = Modifier.padding(top = AppSpacing.xl)
                    )
                } else {
                    Text(
                        text = "${searchResults.size} items found",
                        style = PremiumTheme.typography.bodySmall,
                        color = PremiumTheme.colors.textSecondary,
                        modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.xs)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(AppSpacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                    ) {
                        items(searchResults, key = { it.id }) { item ->
                            MediaThumbnail(
                                item = item,
                                onClick = {
                                    viewModel.executeSearch(query)
                                    onNavigateToMediaViewer(item.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
