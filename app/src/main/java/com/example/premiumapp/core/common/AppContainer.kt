package com.example.premiumapp.core.common

import android.content.Context
import com.example.premiumapp.core.media.MediaEditor
import com.example.premiumapp.core.media.MediaExporter
import com.example.premiumapp.core.media.MediaImporter
import com.example.premiumapp.core.media.MediaManager
import com.example.premiumapp.core.media.ThumbnailGenerator
import com.example.premiumapp.core.permissions.PermissionManager
import com.example.premiumapp.core.storage.StorageManager
import com.example.premiumapp.data.datastore.PreferencesManager
import com.example.premiumapp.data.local.AppDatabase
import com.example.premiumapp.data.repository.ActivityRepositoryImpl
import com.example.premiumapp.data.repository.CollectionRepositoryImpl
import com.example.premiumapp.data.repository.MediaRepositoryImpl
import com.example.premiumapp.data.repository.PreferencesRepositoryImpl
import com.example.premiumapp.data.repository.RecentSearchRepositoryImpl
import com.example.premiumapp.domain.repository.ActivityRepository
import com.example.premiumapp.domain.repository.CollectionRepository
import com.example.premiumapp.domain.repository.MediaRepository
import com.example.premiumapp.domain.repository.PreferencesRepository
import com.example.premiumapp.domain.repository.RecentSearchRepository
import com.example.premiumapp.domain.usecase.AddMediaToCollectionUseCase
import com.example.premiumapp.domain.usecase.CreateCollectionUseCase
import com.example.premiumapp.domain.usecase.DeleteCollectionUseCase
import com.example.premiumapp.domain.usecase.DeleteMediaUseCase
import com.example.premiumapp.domain.usecase.EditMediaUseCase
import com.example.premiumapp.domain.usecase.ExportMediaUseCase
import com.example.premiumapp.domain.usecase.GetStorageInfoUseCase
import com.example.premiumapp.domain.usecase.ImportMediaUseCase
import com.example.premiumapp.domain.usecase.RemoveMediaFromCollectionUseCase
import com.example.premiumapp.domain.usecase.SearchMediaUseCase
import com.example.premiumapp.domain.usecase.ToggleFavoriteUseCase

interface AppContainer {
    val context: Context
    val database: AppDatabase
    val thumbnailGenerator: ThumbnailGenerator
    val mediaImporter: MediaImporter
    val mediaExporter: MediaExporter
    val mediaEditor: MediaEditor
    val mediaManager: MediaManager
    val storageManager: StorageManager
    val permissionManager: PermissionManager
    val preferencesManager: PreferencesManager

    val mediaRepository: MediaRepository
    val collectionRepository: CollectionRepository
    val activityRepository: ActivityRepository
    val recentSearchRepository: RecentSearchRepository
    val preferencesRepository: PreferencesRepository

    val importMediaUseCase: ImportMediaUseCase
    val deleteMediaUseCase: DeleteMediaUseCase
    val toggleFavoriteUseCase: ToggleFavoriteUseCase
    val searchMediaUseCase: SearchMediaUseCase
    val createCollectionUseCase: CreateCollectionUseCase
    val deleteCollectionUseCase: DeleteCollectionUseCase
    val addMediaToCollectionUseCase: AddMediaToCollectionUseCase
    val removeMediaFromCollectionUseCase: RemoveMediaFromCollectionUseCase
    val editMediaUseCase: EditMediaUseCase
    val exportMediaUseCase: ExportMediaUseCase
    val getStorageInfoUseCase: GetStorageInfoUseCase
}

class AppContainerImpl(override val context: Context) : AppContainer {

    override val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    override val thumbnailGenerator: ThumbnailGenerator by lazy {
        ThumbnailGenerator(context)
    }

    override val mediaImporter: MediaImporter by lazy {
        MediaImporter(context, thumbnailGenerator)
    }

    override val mediaExporter: MediaExporter by lazy {
        MediaExporter(context)
    }

    override val mediaEditor: MediaEditor by lazy {
        MediaEditor(context, thumbnailGenerator)
    }

    override val mediaManager: MediaManager by lazy {
        MediaManager(context)
    }

    override val storageManager: StorageManager by lazy {
        StorageManager(context)
    }

    override val permissionManager: PermissionManager by lazy {
        PermissionManager(context)
    }

    override val preferencesManager: PreferencesManager by lazy {
        PreferencesManager(context)
    }

    override val activityRepository: ActivityRepository by lazy {
        ActivityRepositoryImpl(database.activityDao())
    }

    override val recentSearchRepository: RecentSearchRepository by lazy {
        RecentSearchRepositoryImpl(database.recentSearchDao())
    }

    override val mediaRepository: MediaRepository by lazy {
        MediaRepositoryImpl(
            mediaDao = database.mediaDao(),
            mediaImporter = mediaImporter,
            mediaExporter = mediaExporter,
            mediaEditor = mediaEditor,
            activityRepository = activityRepository
        )
    }

    override val collectionRepository: CollectionRepository by lazy {
        CollectionRepositoryImpl(
            collectionDao = database.collectionDao(),
            activityRepository = activityRepository
        )
    }

    override val preferencesRepository: PreferencesRepository by lazy {
        PreferencesRepositoryImpl(
            preferencesManager = preferencesManager
        )
    }

    override val importMediaUseCase: ImportMediaUseCase by lazy {
        ImportMediaUseCase(mediaRepository)
    }

    override val deleteMediaUseCase: DeleteMediaUseCase by lazy {
        DeleteMediaUseCase(mediaRepository)
    }

    override val toggleFavoriteUseCase: ToggleFavoriteUseCase by lazy {
        ToggleFavoriteUseCase(mediaRepository)
    }

    override val searchMediaUseCase: SearchMediaUseCase by lazy {
        SearchMediaUseCase(mediaRepository)
    }

    override val createCollectionUseCase: CreateCollectionUseCase by lazy {
        CreateCollectionUseCase(collectionRepository)
    }

    override val deleteCollectionUseCase: DeleteCollectionUseCase by lazy {
        DeleteCollectionUseCase(collectionRepository)
    }

    override val addMediaToCollectionUseCase: AddMediaToCollectionUseCase by lazy {
        AddMediaToCollectionUseCase(collectionRepository)
    }

    override val removeMediaFromCollectionUseCase: RemoveMediaFromCollectionUseCase by lazy {
        RemoveMediaFromCollectionUseCase(collectionRepository)
    }

    override val editMediaUseCase: EditMediaUseCase by lazy {
        EditMediaUseCase(mediaRepository)
    }

    override val exportMediaUseCase: ExportMediaUseCase by lazy {
        ExportMediaUseCase(mediaRepository)
    }

    override val getStorageInfoUseCase: GetStorageInfoUseCase by lazy {
        GetStorageInfoUseCase(storageManager)
    }
}

fun AppContainer(context: Context): AppContainer = AppContainerImpl(context)
