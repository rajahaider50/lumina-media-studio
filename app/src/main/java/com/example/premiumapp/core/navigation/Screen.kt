package com.example.premiumapp.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    // 01 Splash
    data object Splash : Screen("splash")

    // 02 Onboarding
    data object Onboarding : Screen("onboarding")

    // 03 Home
    data object Home : Screen("home")

    // 04 Explore
    data object Explore : Screen("explore")

    // 05 Search
    data object Search : Screen("search")

    // 06 Collections
    data object Collections : Screen("collections")

    // 07 Create Collection
    data object CreateCollection : Screen("create_collection")

    // 08 Collection Details
    data object CollectionDetails : Screen("collection_details/{collectionId}") {
        fun createRoute(collectionId: Long): String = "collection_details/$collectionId"
    }

    // 09 Import Media
    data object ImportMedia : Screen("import_media")

    // 10 Media Details
    data object MediaDetails : Screen("media_details/{mediaId}") {
        fun createRoute(mediaId: Long): String = "media_details/$mediaId"
    }

    // 11 Media Viewer
    data object MediaViewer : Screen("media_viewer/{mediaId}") {
        fun createRoute(mediaId: Long): String = "media_viewer/$mediaId"
    }

    // 12 Editor
    data object Editor : Screen("editor/{mediaId}") {
        fun createRoute(mediaId: Long): String = "editor/$mediaId"
    }

    // 13 Favorites
    data object Favorites : Screen("favorites")

    // 14 Recent Activity
    data object RecentActivity : Screen("recent_activity")

    // 15 Profile
    data object Profile : Screen("profile")

    // 16 Settings
    data object Settings : Screen("settings")

    // 17 Appearance
    data object Appearance : Screen("appearance")

    // 18 Permissions Center
    data object Permissions : Screen("permissions")

    // 19 Storage Center
    data object Storage : Screen("storage")

    // 20 Notifications Center
    data object Notifications : Screen("notifications")

    // 21 Media Selection Mode
    data object Selection : Screen("selection")

    // 22 Share / Export
    data object ShareExport : Screen("share_export/{mediaId}") {
        fun createRoute(mediaId: Long): String = "share_export/$mediaId"
    }

    // 23 Help & Privacy
    data object Help : Screen("help")

    // 24 About
    data object About : Screen("about")

    // 25 Error / Offline Hub
    data object Errors : Screen("errors?type={type}") {
        fun createRoute(type: String = "generic"): String = "errors?type=$type"
    }
}

data class BottomNavItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val screen: Screen
)

val BottomNavItems = listOf(
    BottomNavItem("Home", androidx.compose.material.icons.Icons.Rounded.Home, Screen.Home),
    BottomNavItem("Explore", androidx.compose.material.icons.Icons.Rounded.Explore, Screen.Explore),
    BottomNavItem("Collections", androidx.compose.material.icons.Icons.Rounded.Collections, Screen.Collections),
    BottomNavItem("Favorites", androidx.compose.material.icons.Icons.Rounded.Favorite, Screen.Favorites),
    BottomNavItem("Profile", androidx.compose.material.icons.Icons.Rounded.Person, Screen.Profile)
)
