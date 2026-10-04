package com.example.premiumapp.core.media

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.premiumapp.core.common.AppError
import com.example.premiumapp.core.common.AppResult
import com.example.premiumapp.domain.model.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MediaExporter(private val context: Context) {

    suspend fun exportToGallery(mediaItem: MediaItem): AppResult<Uri> = withContext(Dispatchers.IO) {
        try {
            val sourceFile = mediaItem.localPath?.let { File(it) }
            if (sourceFile == null || !sourceFile.exists()) {
                return@withContext AppResult.Error(AppError.MediaNotFound("Source media file not found"))
            }

            val isVideo = mediaItem.isVideo
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, mediaItem.displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, mediaItem.mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val relativeDir = if (isVideo) "${Environment.DIRECTORY_MOVIES}/LuminaMedia" else "${Environment.DIRECTORY_PICTURES}/LuminaMedia"
                    put(MediaStore.MediaColumns.RELATIVE_PATH, relativeDir)
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val collection = if (isVideo) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
            }

            val uri = context.contentResolver.insert(collection, contentValues)
                ?: return@withContext AppResult.Error(AppError.ExportFailed("Could not create MediaStore entry"))

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                FileInputStream(sourceFile).use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                context.contentResolver.update(uri, contentValues, null, null)
            }

            AppResult.Success(uri)
        } catch (e: Exception) {
            e.printStackTrace()
            AppResult.Error(AppError.ExportFailed("Failed to export to gallery: ${e.localizedMessage}", e))
        }
    }

    suspend fun exportToDestinationUri(mediaItem: MediaItem, destinationUri: Uri): AppResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val sourceFile = mediaItem.localPath?.let { File(it) }
            if (sourceFile == null || !sourceFile.exists()) {
                return@withContext AppResult.Error(AppError.MediaNotFound("Source media file not found"))
            }

            context.contentResolver.openOutputStream(destinationUri)?.use { outputStream ->
                FileInputStream(sourceFile).use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: return@withContext AppResult.Error(AppError.ExportFailed("Could not write to destination"))

            AppResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            AppResult.Error(AppError.ExportFailed("SAF export failed: ${e.localizedMessage}", e))
        }
    }

    fun createShareIntent(mediaItem: MediaItem): Intent? {
        val file = mediaItem.localPath?.let { File(it) } ?: return null
        if (!file.exists()) return null

        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        return Intent(Intent.ACTION_SEND).apply {
            type = mediaItem.mimeType
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, mediaItem.displayName)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun createShareMultipleIntent(mediaItems: List<MediaItem>): Intent? {
        val uris = ArrayList<Uri>()
        for (item in mediaItems) {
            val file = item.localPath?.let { File(it) } ?: continue
            if (file.exists()) {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                uris.add(uri)
            }
        }
        if (uris.isEmpty()) return null

        return Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "*/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
