package com.example.premiumapp.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.premiumapp.core.di.AppContainer
import com.example.premiumapp.design.components.BottomBarDestination
import com.example.premiumapp.design.components.PremiumBottomBar
import com.example.premiumapp.design.theme.PremiumTheme
import com.example.premiumapp.feature.about.AboutScreen
import com.example.premiumapp.feature.activity.RecentActivityScreen
import com.example.premiumapp.feature.activity.RecentActivityViewModel
import com.example.premiumapp.feature.appearance.AppearanceScreen
import com.example.premiumapp.feature.appearance.AppearanceViewModel
import com.example.premiumapp.feature.collectiondetails.CollectionDetailsScreen
import com.example.premiumapp.feature.collectiondetails.CollectionDetailsViewModel
import com.example.premiumapp.feature.collections.CollectionsScreen
import com.example.premiumapp.feature.collections.CollectionsViewModel
import com.example.premiumapp.feature.createcollection.CreateCollectionScreen
import com.example.premiumapp.feature.createcollection.CreateCollectionViewModel
import com.example.premiumapp.feature.editor.EditorScreen
import com.example.premiumapp.feature.editor.EditorViewModel
import com.example.premiumapp.feature.errors.ErrorHubScreen
import com.example.premiumapp.feature.explore.ExploreScreen
import com.example.premiumapp.feature.explore.ExploreViewModel
import com.example.premiumapp.feature.favorites.FavoritesScreen
import com.example.premiumapp.feature.favorites.FavoritesViewModel
import com.example.premiumapp.feature.help.HelpPrivacyScreen
import com.example.premiumapp.feature.home.HomeScreen
import com.example.premiumapp.feature.home.HomeViewModel
import com.example.premiumapp.feature.importmedia.ImportMediaScreen
import com.example.premiumapp.feature.importmedia.ImportMediaViewModel
import com.example.premiumapp.feature.mediadetails.MediaDetailsScreen
import com.example.premiumapp.feature.mediadetails.MediaDetailsViewModel
import com.example.premiumapp.feature.mediaviewer.MediaViewerScreen
import com.example.premiumapp.feature.mediaviewer.MediaViewerViewModel
import com.example.premiumapp.feature.notifications.NotificationsCenterScreen
import com.example.premiumapp.feature.notifications.NotificationsCenterViewModel
import com.example.premiumapp.feature.onboarding.OnboardingScreen
import com.example.premiumapp.feature.onboarding.OnboardingViewModel
import com.example.premiumapp.feature.permissions.PermissionsScreen
import com.example.premiumapp.feature.permissions.PermissionsViewModel
import com.example.premiumapp.feature.profile.ProfileScreen
import com.example.premiumapp.feature.profile.ProfileViewModel
import com.example.premiumapp.feature.search.SearchScreen
import com.example.premiumapp.feature.search.SearchViewModel
import com.example.premiumapp.feature.selection.SelectionScreen
import com.example.premiumapp.feature.selection.SelectionViewModel
import com.example.premiumapp.feature.settings.SettingsScreen
import com.example.premiumapp.feature.settings.SettingsViewModel
import com.example.premiumapp.feature.share.ShareExportScreen
import com.example.premiumapp.feature.share.ShareExportViewModel
import com.example.premiumapp.feature.splash.SplashScreen
import com.example.premiumapp.feature.splash.SplashViewModel
import com.example.premiumapp.feature.storage.StorageScreen
import com.example.premiumapp.feature.storage.StorageViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    container: AppContainer,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Splash.route

    val primaryRoutes = setOf(
        Screen.Home.route,
        Screen.Explore.route,
        Screen.Collections.route,
        Screen.Favorites.route,
        Screen.Profile.route
    )
    val showBottomBar = currentRoute in primaryRoutes

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = PremiumTheme.colors.background,
        bottomBar = {
            if (showBottomBar) {
                PremiumBottomBar(
                    currentRoute = currentRoute,
                    onNavigateToDestination = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // 01 Splash
            composable(Screen.Splash.route) {
                val viewModel = rememberViewModel { SplashViewModel(container.preferencesRepository) }
                SplashScreen(
                    viewModel = viewModel,
                    onNavigateToOnboarding = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // 02 Onboarding
            composable(Screen.Onboarding.route) {
                val viewModel = rememberViewModel { OnboardingViewModel(container.preferencesRepository) }
                OnboardingScreen(
                    viewModel = viewModel,
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // 03 Home
            composable(Screen.Home.route) {
                val viewModel = rememberViewModel {
                    HomeViewModel(
                        mediaRepository = container.mediaRepository,
                        collectionRepository = container.collectionRepository,
                        preferencesRepository = container.preferencesRepository,
                        getStorageInfoUseCase = container.getStorageInfoUseCase
                    )
                }
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToMediaViewer = { id -> navController.navigate(Screen.MediaViewer.createRoute(id)) },
                    onNavigateToImport = { navController.navigate(Screen.ImportMedia.route) },
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToCreateCollection = { navController.navigate(Screen.CreateCollection.route) },
                    onNavigateToCollectionDetails = { id -> navController.navigate(Screen.CollectionDetails.createRoute(id)) },
                    onNavigateToExplore = { navController.navigate(Screen.Explore.route) },
                    onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                    onNavigateToStorage = { navController.navigate(Screen.Storage.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            // 04 Explore
            composable(Screen.Explore.route) {
                val viewModel = rememberViewModel {
                    ExploreViewModel(
                        mediaRepository = container.mediaRepository,
                        preferencesRepository = container.preferencesRepository
                    )
                }
                ExploreScreen(
                    viewModel = viewModel,
                    onNavigateToMediaViewer = { id -> navController.navigate(Screen.MediaViewer.createRoute(id)) },
                    onNavigateToSelection = { navController.navigate(Screen.Selection.route) },
                    onNavigateToImport = { navController.navigate(Screen.ImportMedia.route) }
                )
            }

            // 05 Search
            composable(Screen.Search.route) {
                val viewModel = rememberViewModel {
                    SearchViewModel(
                        mediaRepository = container.mediaRepository,
                        recentSearchRepository = container.recentSearchRepository
                    )
                }
                SearchScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMediaViewer = { id -> navController.navigate(Screen.MediaViewer.createRoute(id)) }
                )
            }

            // 06 Collections
            composable(Screen.Collections.route) {
                val viewModel = rememberViewModel { CollectionsViewModel(container.collectionRepository) }
                CollectionsScreen(
                    viewModel = viewModel,
                    onNavigateToCreateCollection = { navController.navigate(Screen.CreateCollection.route) },
                    onNavigateToCollectionDetails = { id -> navController.navigate(Screen.CollectionDetails.createRoute(id)) }
                )
            }

            // 07 Create Collection
            composable(Screen.CreateCollection.route) {
                val viewModel = rememberViewModel { CreateCollectionViewModel(container.createCollectionUseCase) }
                CreateCollectionScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onCollectionCreated = { id ->
                        navController.navigate(Screen.CollectionDetails.createRoute(id)) {
                            popUpTo(Screen.CreateCollection.route) { inclusive = true }
                        }
                    }
                )
            }

            // 08 Collection Details
            composable(
                route = Screen.CollectionDetails.route,
                arguments = listOf(navArgument("collectionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val collectionId = backStackEntry.arguments?.getLong("collectionId") ?: 0L
                val viewModel = rememberViewModel(key = "col_$collectionId") {
                    CollectionDetailsViewModel(
                        collectionId = collectionId,
                        collectionRepository = container.collectionRepository,
                        mediaRepository = container.mediaRepository
                    )
                }
                CollectionDetailsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMediaViewer = { id -> navController.navigate(Screen.MediaViewer.createRoute(id)) },
                    onNavigateToImport = { navController.navigate(Screen.ImportMedia.route) }
                )
            }

            // 09 Import Media
            composable(Screen.ImportMedia.route) {
                val viewModel = rememberViewModel { ImportMediaViewModel(container.importMediaUseCase) }
                ImportMediaScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMediaViewer = { id -> navController.navigate(Screen.MediaViewer.createRoute(id)) }
                )
            }

            // 10 Media Details
            composable(
                route = Screen.MediaDetails.route,
                arguments = listOf(navArgument("mediaId") { type = NavType.LongType })
            ) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getLong("mediaId") ?: 0L
                val viewModel = rememberViewModel(key = "media_det_$mediaId") {
                    MediaDetailsViewModel(
                        mediaId = mediaId,
                        mediaRepository = container.mediaRepository,
                        collectionRepository = container.collectionRepository
                    )
                }
                MediaDetailsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToViewer = { id -> navController.navigate(Screen.MediaViewer.createRoute(id)) },
                    onNavigateToEditor = { id -> navController.navigate(Screen.Editor.createRoute(id)) },
                    onNavigateToExport = { id -> navController.navigate(Screen.ShareExport.createRoute(id)) },
                    onNavigateToShare = { id -> navController.navigate(Screen.ShareExport.createRoute(id)) }
                )
            }

            // 11 Media Viewer
            composable(
                route = Screen.MediaViewer.route,
                arguments = listOf(navArgument("mediaId") { type = NavType.LongType })
            ) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getLong("mediaId") ?: 0L
                val viewModel = rememberViewModel(key = "viewer_$mediaId") {
                    MediaViewerViewModel(
                        mediaId = mediaId,
                        mediaRepository = container.mediaRepository
                    )
                }
                MediaViewerScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetails = { id -> navController.navigate(Screen.MediaDetails.createRoute(id)) },
                    onNavigateToEditor = { id -> navController.navigate(Screen.Editor.createRoute(id)) }
                )
            }

            // 12 Editor
            composable(
                route = Screen.Editor.route,
                arguments = listOf(navArgument("mediaId") { type = NavType.LongType })
            ) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getLong("mediaId") ?: 0L
                val viewModel = rememberViewModel(key = "editor_$mediaId") {
                    EditorViewModel(
                        mediaId = mediaId,
                        mediaRepository = container.mediaRepository,
                        editMediaUseCase = container.editMediaUseCase
                    )
                }
                EditorScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMediaViewer = { id ->
                        navController.navigate(Screen.MediaViewer.createRoute(id)) {
                            popUpTo(Screen.Editor.route) { inclusive = true }
                        }
                    }
                )
            }

            // 13 Favorites
            composable(Screen.Favorites.route) {
                val viewModel = rememberViewModel { FavoritesViewModel(container.mediaRepository) }
                FavoritesScreen(
                    viewModel = viewModel,
                    onNavigateToMediaViewer = { id -> navController.navigate(Screen.MediaViewer.createRoute(id)) },
                    onNavigateToExplore = { navController.navigate(Screen.Explore.route) }
                )
            }

            // 14 Recent Activity
            composable(Screen.RecentActivity.route) {
                val viewModel = rememberViewModel { RecentActivityViewModel(container.activityRepository) }
                RecentActivityScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 15 Profile
            composable(Screen.Profile.route) {
                val viewModel = rememberViewModel {
                    ProfileViewModel(
                        preferencesRepository = container.preferencesRepository,
                        mediaRepository = container.mediaRepository,
                        collectionRepository = container.collectionRepository,
                        getStorageInfoUseCase = container.getStorageInfoUseCase
                    )
                }
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToAppearance = { navController.navigate(Screen.Appearance.route) },
                    onNavigateToPermissions = { navController.navigate(Screen.Permissions.route) },
                    onNavigateToStorage = { navController.navigate(Screen.Storage.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onNavigateToActivity = { navController.navigate(Screen.RecentActivity.route) },
                    onNavigateToHelp = { navController.navigate(Screen.Help.route) },
                    onNavigateToAbout = { navController.navigate(Screen.About.route) }
                )
            }

            // 16 Settings
            composable(Screen.Settings.route) {
                val viewModel = rememberViewModel {
                    SettingsViewModel(
                        preferencesRepository = container.preferencesRepository,
                        activityRepository = container.activityRepository,
                        recentSearchRepository = container.recentSearchRepository
                    )
                }
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAppearance = { navController.navigate(Screen.Appearance.route) },
                    onNavigateToPermissions = { navController.navigate(Screen.Permissions.route) },
                    onNavigateToStorage = { navController.navigate(Screen.Storage.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onNavigateToHelp = { navController.navigate(Screen.Help.route) },
                    onNavigateToAbout = { navController.navigate(Screen.About.route) }
                )
            }

            // 17 Appearance
            composable(Screen.Appearance.route) {
                val viewModel = rememberViewModel { AppearanceViewModel(container.preferencesRepository) }
                AppearanceScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 18 Permissions Center
            composable(Screen.Permissions.route) {
                val viewModel = rememberViewModel { PermissionsViewModel(container.permissionManager) }
                PermissionsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 19 Storage Center
            composable(Screen.Storage.route) {
                val viewModel = rememberViewModel {
                    StorageViewModel(
                        getStorageInfoUseCase = container.getStorageInfoUseCase,
                        mediaRepository = container.mediaRepository,
                        deleteMediaUseCase = container.deleteMediaUseCase,
                        toggleFavoriteUseCase = container.toggleFavoriteUseCase
                    )
                }
                StorageScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMedia = { id -> navController.navigate(Screen.MediaViewer.createRoute(id)) }
                )
            }

            // 20 Notifications Center
            composable(Screen.Notifications.route) {
                val viewModel = rememberViewModel {
                    NotificationsCenterViewModel(
                        preferencesRepository = container.preferencesRepository,
                        activityRepository = container.activityRepository
                    )
                }
                NotificationsCenterScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 21 Selection Mode
            composable(Screen.Selection.route) {
                val viewModel = rememberViewModel {
                    SelectionViewModel(
                        mediaRepository = container.mediaRepository,
                        collectionRepository = container.collectionRepository
                    )
                }
                SelectionScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 22 Share / Export
            composable(
                route = Screen.ShareExport.route,
                arguments = listOf(navArgument("mediaId") { type = NavType.LongType })
            ) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getLong("mediaId") ?: 0L
                val viewModel = rememberViewModel(key = "share_$mediaId") {
                    ShareExportViewModel(
                        mediaId = mediaId,
                        mediaRepository = container.mediaRepository,
                        exportMediaUseCase = container.exportMediaUseCase,
                        mediaExporter = container.mediaExporter
                    )
                }
                ShareExportScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToViewer = { id -> navController.navigate(Screen.MediaViewer.createRoute(id)) }
                )
            }

            // 23 Help & Privacy
            composable(Screen.Help.route) {
                HelpPrivacyScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 24 About
            composable(Screen.About.route) {
                AboutScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 25 Error / Offline State Hub
            composable(
                route = Screen.Errors.route,
                arguments = listOf(navArgument("type") {
                    type = NavType.StringType
                    defaultValue = "OFFLINE"
                })
            ) { backStackEntry ->
                val type = backStackEntry.arguments?.getString("type") ?: "OFFLINE"
                ErrorHubScreen(
                    initialType = type,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPermissions = { navController.navigate(Screen.Permissions.route) },
                    onNavigateToStorage = { navController.navigate(Screen.Storage.route) },
                    onNavigateToImport = { navController.navigate(Screen.ImportMedia.route) }
                )
            }
        }
    }
}

@Composable
fun LuminaNavGraph(
    navController: NavHostController,
    container: AppContainer,
    modifier: Modifier = Modifier
) {
    AppNavGraph(navController = navController, container = container, modifier = modifier)
}

@Suppress("UNCHECKED_CAST")
@Composable
inline fun <reified T : androidx.lifecycle.ViewModel> rememberViewModel(
    key: String? = null,
    crossinline creator: () -> T
): T {
    return androidx.lifecycle.viewmodel.compose.viewModel(
        key = key,
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <VM : androidx.lifecycle.ViewModel> create(modelClass: Class<VM>): VM {
                return creator() as VM
            }
        }
    )
}
