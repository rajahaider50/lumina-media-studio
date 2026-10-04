package com.example.premiumapp.feature.notifications

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.premiumapp.core.extensions.formatDateTime
import com.example.premiumapp.design.components.EmptyState
import com.example.premiumapp.design.components.PremiumCard
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.components.SectionHeader
import com.example.premiumapp.design.components.SettingRow
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.AppShapes
import com.example.premiumapp.design.theme.PremiumTheme

@Composable
fun NotificationsCenterScreen(
    viewModel: NotificationsCenterViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = "Notifications Center",
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
                .padding(AppSpacing.md)
        ) {
            // Notification Channels Preference
            SectionHeader(title = "Notification Channels")

            PremiumCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.large,
                backgroundColor = PremiumTheme.colors.surface,
                contentPadding = 0.dp
            ) {
                SettingRow(
                    title = "Media Operations Alerts",
                    subtitle = "Notify when large imports and exports finish",
                    icon = AppIcons.Notifications,
                    checked = state.notificationsEnabled,
                    onCheckedChange = { viewModel.setNotificationsEnabled(it) }
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.lg))

            // Notification History
            SectionHeader(title = "Notification History")

            if (state.notificationHistory.isEmpty()) {
                EmptyState(
                    title = "No Notifications",
                    description = "Updates for completed exports, imports, and system alerts will appear here.",
                    icon = AppIcons.Notifications
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                ) {
                    items(state.notificationHistory, key = { it.id }) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = AppSpacing.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (item.isAlert) PremiumTheme.colors.warning.copy(alpha = 0.15f)
                                        else PremiumTheme.colors.primary.copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (item.isAlert) AppIcons.Warning else AppIcons.Success,
                                    contentDescription = null,
                                    tint = if (item.isAlert) PremiumTheme.colors.warning else PremiumTheme.colors.primary,
                                    modifier = Modifier.size(AppIconSize.standard)
                                )
                            }

                            Spacer(modifier = Modifier.width(AppSpacing.md))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = PremiumTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = PremiumTheme.colors.textPrimary
                                )
                                Text(
                                    text = item.message,
                                    style = PremiumTheme.typography.bodyMedium,
                                    color = PremiumTheme.colors.textSecondary
                                )
                                Text(
                                    text = item.timestamp.formatDateTime(),
                                    style = PremiumTheme.typography.bodySmall,
                                    color = PremiumTheme.colors.textTertiary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
