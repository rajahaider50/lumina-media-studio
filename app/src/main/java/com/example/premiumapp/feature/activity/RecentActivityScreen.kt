package com.example.premiumapp.feature.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.premiumapp.core.extensions.formatDateTime
import com.example.premiumapp.design.components.EmptyState
import com.example.premiumapp.design.components.PremiumConfirmDialog
import com.example.premiumapp.design.components.PremiumIconButton
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.PremiumTheme
import com.example.premiumapp.domain.model.ActivityType

@Composable
fun RecentActivityScreen(
    viewModel: RecentActivityViewModel,
    onNavigateBack: () -> Unit
) {
    val activities by viewModel.activities.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = "Recent Activity",
                subtitle = "${activities.size} logged events",
                navigationIcon = AppIcons.Back,
                navigationIconContentDescription = "Go Back",
                onNavigationClick = onNavigateBack,
                actions = {
                    if (activities.isNotEmpty()) {
                        PremiumIconButton(
                            icon = AppIcons.Delete,
                            contentDescription = "Clear Activity History",
                            onClick = { showClearDialog = true }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (activities.isEmpty()) {
                EmptyState(
                    title = "No Recent Activity",
                    description = "Actions such as media import, editing, collections, and favorites will appear here.",
                    icon = AppIcons.Activity
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(AppSpacing.md)
                ) {
                    items(activities, key = { it.id }) { activity ->
                        val icon = when (activity.type) {
                            ActivityType.IMPORTED -> AppIcons.Import
                            ActivityType.CREATED_COLLECTION -> AppIcons.Folder
                            ActivityType.ADDED_TO_COLLECTION -> AppIcons.Add
                            ActivityType.REMOVED_FROM_COLLECTION -> AppIcons.Close
                            ActivityType.EDITED -> AppIcons.Edit
                            ActivityType.EXPORTED -> AppIcons.Export
                            ActivityType.DELETED -> AppIcons.Delete
                            ActivityType.FAVORITED -> AppIcons.FavoritesFilled
                            ActivityType.UNFAVORITED -> AppIcons.FavoritesOutlined
                            ActivityType.OPENED -> AppIcons.Play
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = AppSpacing.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(PremiumTheme.colors.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = PremiumTheme.colors.primary,
                                    modifier = Modifier.size(AppIconSize.standard)
                                )
                            }

                            Spacer(modifier = Modifier.width(AppSpacing.md))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = activity.description,
                                    style = PremiumTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                                    color = PremiumTheme.colors.textPrimary
                                )
                                Text(
                                    text = activity.timestamp.formatDateTime(),
                                    style = PremiumTheme.typography.bodySmall,
                                    color = PremiumTheme.colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }

            if (showClearDialog) {
                PremiumConfirmDialog(
                    title = "Clear Activity History?",
                    message = "This removes recent activity log records but does NOT delete your original photos, videos, or collections.",
                    confirmText = "Clear",
                    isDestructive = true,
                    onConfirm = {
                        viewModel.clearAllActivities()
                        showClearDialog = false
                    },
                    onDismiss = { showClearDialog = false }
                )
            }
        }
    }
}
