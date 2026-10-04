package com.example.premiumapp.design.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.premiumapp.design.dimensions.AppTouchTarget
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.PremiumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: ImageVector? = null,
    navigationIconContentDescription: String? = "Back",
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            if (subtitle == null) {
                Text(
                    text = title,
                    style = PremiumTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = PremiumTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                androidx.compose.foundation.layout.Column(
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = PremiumTheme.colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        style = PremiumTheme.typography.bodySmall,
                        color = PremiumTheme.colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        navigationIcon = {
            if (navigationIcon != null && onNavigationClick != null) {
                IconButton(
                    onClick = onNavigationClick,
                    modifier = Modifier.androidx.compose.foundation.layout.size(AppTouchTarget.minSize)
                ) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationIconContentDescription,
                        tint = PremiumTheme.colors.textPrimary
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = PremiumTheme.colors.background,
            scrolledContainerColor = PremiumTheme.colors.surfaceVariant
        ),
        windowInsets = WindowInsets.statusBars
    )
}
