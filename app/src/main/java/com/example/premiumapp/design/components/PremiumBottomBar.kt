package com.example.premiumapp.design.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.PremiumTheme

enum class BottomBarDestination(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("home", "Home", AppIcons.HomeFilled, AppIcons.HomeOutlined),
    EXPLORE("explore", "Explore", AppIcons.ExploreFilled, AppIcons.ExploreOutlined),
    COLLECTIONS("collections", "Collections", AppIcons.CollectionsFilled, AppIcons.CollectionsOutlined),
    FAVORITES("favorites", "Favorites", AppIcons.FavoritesFilled, AppIcons.FavoritesOutlined),
    PROFILE("profile", "Profile", AppIcons.ProfileFilled, AppIcons.ProfileOutlined)
}

@Composable
fun PremiumBottomBar(
    currentRoute: String,
    onNavigateToDestination: (BottomBarDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = PremiumTheme.colors.surface,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        BottomBarDestination.values().forEach { destination ->
            val isSelected = currentRoute == destination.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = destination.title
                    )
                },
                label = {
                    Text(
                        text = destination.title,
                        style = PremiumTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PremiumTheme.colors.primary,
                    selectedTextColor = PremiumTheme.colors.primary,
                    indicatorColor = PremiumTheme.colors.surfaceVariant,
                    unselectedIconColor = PremiumTheme.colors.textSecondary,
                    unselectedTextColor = PremiumTheme.colors.textSecondary
                )
            )
        }
    }
}
