package com.example.premiumapp.core.media

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import com.example.premiumapp.core.common.AppError
import com.example.premiumapp.core.common.AppResult
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.model.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.UUID

class MediaImporter(
    private val context: Context,
    private val thumbnailGenerator: ThumbnailGenerator
) {

    private val mediaStorageDir: File by lazy {
        File(context.filesDir, "media").apply { if (!exists()) mkdirs() }
    }

    suspend fun importMedia(uri: Uri): AppResult<MediaItem> = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: getMimeTypeFromExtension(uri.toString()) ?: "application/octet-stream"
            val isVideo = mimeType.startsWith("video/")
            val isImage = mimeType.startsWith("image/")

            if (!isImage && !isVideo) {
                return@withContext AppResult.Error(AppError.MediaCorrupted("Unsupported media format: $mimeType"))
            }

            // Extract display name and size from content provider
            var displayName = "media_${System.currentTimeMillis()}"
            var reportedSize: Long = 0

            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        val name = cursor.getString(nameIndex)
                        if (!name.isNullOrBlank()) displayName = name
                    }
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1) {
                        reportedSize = cursor.getLong(sizeIndex)
                    }
                }
            }

            // Generate clean extension
            val extension = if (isVideo) {
                if (displayName.contains(".")) displayName.substringAfterLast(".") else "mp4"
            } else {
                if (displayName.contains(".")) displayName.substringAfterLast(".") else "jpg"
            }

            // Copy file safely into app-private storage
            val targetFile = File(mediaStorageDir, "item_${UUID.randomUUID()}.$extension")
            val md5Digest = MessageDigest.getInstance("MD5")

            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        md5Digest.update(buffer, 0, bytesRead)
                    }
                }
            } ?: return@withContext AppResult.Error(AppError.MediaNotFound("Unable to open media stream"))

            val actualSize = targetFile.length()
            val hash = md5Digest.digest().joinToString("") { "%02x".format(it) }

            // Extract dimensions and duration
            var width: Int? = null
            var height: Int? = null
            var durationMs: Long? = null
            val localUri = Uri.fromFile(targetFile)

            if (isVideo) {
                try {
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(targetFile.absolutePath)
                    val widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                    val heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                    val durStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    width = widthStr?.toIntOrNull()
                    height = heightStr?.toIntOrNull()
                    durationMs = durStr?.toLongOrNull()
                    retriever.release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                try {
                    val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeFile(targetFile.absolutePath, boundsOptions)
                    width = boundsOptions.outWidth
                    height = boundsOptions.outHeight
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Generate thumbnail
            val thumbUri = if (isVideo) {
                thumbnailGenerator.generateVideoThumbnail(localUri)
            } else {
                thumbnailGenerator.generateImageThumbnail(localUri)
            }

            val mediaItem = MediaItem(
                uri = localUri.toString(),
                localPath = targetFile.absolutePath,
                displayName = displayName,
                mimeType = mimeType,
                mediaType = if (isVideo) MediaType.VIDEO else MediaType.IMAGE,
                sizeBytes = actualSize,
                width = width,
                height = height,
                durationMs = durationMs,
                dateAdded = System.currentTimeMillis(),
                dateModified = targetFile.lastModified(),
                isFavorite = false,
                thumbnailUri = thumbUri,
                hash = hash
            )

            AppResult.Success(mediaItem)
        } catch (e: Exception) {
            e.printStackTrace()
            AppResult.Error(AppError.ImportFailed("Import failed: ${e.localizedMessage}", e))
        }
    }

    private fun getMimeTypeFromExtension(path: String): String? {
        val ext = path.substringAfterLast(".", "").lowercase()
        return when (ext) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            "gif" -> "image/gif"
            "mp4" -> "video/mp4"
            "mkv" -> "video/x-matroska"
            "webm" -> "video/webm"
            "mov" -> "video/quicktime"
            else -> null
        }
    }
}
