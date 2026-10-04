package com.example.premiumapp.core.media

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.example.premiumapp.core.common.Constants
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.model.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.security.MessageDigest
import java.util.UUID

data class ImportedMediaResult(
    val localFile: File,
    val thumbnailFile: File?,
    val displayName: String,
    val mimeType: String,
    val mediaType: MediaType,
    val sizeBytes: Long,
    val width: Int,
    val height: Int,
    val durationMs: Long?,
    val hash: String
)

class MediaManager(private val context: Context) {

    private val mediaDirectory: File
        get() = File(context.filesDir, Constants.MEDIA_DIR).apply { if (!exists()) mkdirs() }

    private val thumbnailDirectory: File
        get() = File(context.filesDir, Constants.THUMBNAIL_DIR).apply { if (!exists()) mkdirs() }

    private val exportDirectory: File
        get() = File(context.filesDir, Constants.EXPORT_DIR).apply { if (!exists()) mkdirs() }

    suspend fun importFromUri(sourceUri: Uri): ImportedMediaResult = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val mimeType = resolver.getType(sourceUri) ?: getMimeTypeFromExtension(sourceUri.toString())
        val isVideo = mimeType.startsWith("video")
        val mediaType = if (isVideo) MediaType.VIDEO else MediaType.IMAGE

        val originalName = queryDisplayName(resolver, sourceUri) ?: "media_${System.currentTimeMillis()}"
        val extension = getExtension(originalName, isVideo)
        val uniqueBase = UUID.randomUUID().toString()
        val destFile = File(mediaDirectory, "$uniqueBase.$extension")

        // Safe stream copy with SHA-256 hash calculation
        val digest = MessageDigest.getInstance("SHA-256")
        resolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                    digest.update(buffer, 0, bytesRead)
                }
                output.flush()
            }
        } ?: throw IllegalStateException("Could not open stream for URI: $sourceUri")

        val hash = digest.digest().joinToString("") { "%02x".format(it) }
        val sizeBytes = destFile.length()

        // Extract technical metadata
        var width = 0
        var height = 0
        var durationMs: Long? = null

        val thumbnailFile = File(thumbnailDirectory, "${uniqueBase}_thumb.jpg")

        if (isVideo) {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(destFile.absolutePath)
                val widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                val heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                width = widthStr?.toIntOrNull() ?: 0
                height = heightStr?.toIntOrNull() ?: 0
                durationMs = durationStr?.toLongOrNull()

                // Generate video frame thumbnail
                val frame = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                if (frame != null) {
                    saveThumbnail(frame, thumbnailFile)
                    frame.recycle()
                }
            } catch (_: Exception) {
                // Video metadata fallback
            } finally {
                retriever.release()
            }
        } else {
            // Image metadata extraction
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(destFile.absolutePath, options)
            width = options.outWidth
            height = options.outHeight

            // Generate image thumbnail
            generateImageThumbnail(destFile, thumbnailFile)
        }

        ImportedMediaResult(
            localFile = destFile,
            thumbnailFile = if (thumbnailFile.exists()) thumbnailFile else null,
            displayName = originalName,
            mimeType = mimeType,
            mediaType = mediaType,
            sizeBytes = sizeBytes,
            width = width,
            height = height,
            durationMs = durationMs,
            hash = hash
        )
    }

    private fun generateImageThumbnail(sourceFile: File, destFile: File) {
        try {
            val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(sourceFile.absolutePath, boundsOptions)

            val maxDimension = 512
            var inSampleSize = 1
            if (boundsOptions.outHeight > maxDimension || boundsOptions.outWidth > maxDimension) {
                val halfHeight = boundsOptions.outHeight / 2
                val halfWidth = boundsOptions.outWidth / 2
                while ((halfHeight / inSampleSize) >= maxDimension && (halfWidth / inSampleSize) >= maxDimension) {
                    inSampleSize *= 2
                }
            }

            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            val bitmap = BitmapFactory.decodeFile(sourceFile.absolutePath, decodeOptions)
            if (bitmap != null) {
                saveThumbnail(bitmap, destFile)
                bitmap.recycle()
            }
        } catch (_: Exception) {
            // Thumbnail failure non-fatal
        }
    }

    private fun saveThumbnail(bitmap: Bitmap, destFile: File) {
        FileOutputStream(destFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            out.flush()
        }
    }

    suspend fun editImage(
        sourceItem: MediaItem,
        cropRect: Rect? = null,
        rotateDegrees: Float = 0f,
        flipHorizontal: Boolean = false,
        flipVertical: Boolean = false,
        brightness: Float = 0f, // -1.0 to 1.0
        contrast: Float = 1.0f,  // 0.5 to 2.0
        saturation: Float = 1.0f // 0.0 to 2.0
    ): MediaItem = withContext(Dispatchers.IO) {
        val originalFile = File(sourceItem.localPath)
        val originalBitmap = BitmapFactory.decodeFile(originalFile.absolutePath)
            ?: throw IllegalStateException("Failed to load source image for editing")

        // 1. Crop if specified
        val cropped = if (cropRect != null && cropRect.width() > 0 && cropRect.height() > 0) {
            val validLeft = cropRect.left.coerceIn(0, originalBitmap.width)
            val validTop = cropRect.top.coerceIn(0, originalBitmap.height)
            val validWidth = cropRect.width().coerceAtMost(originalBitmap.width - validLeft)
            val validHeight = cropRect.height().coerceAtMost(originalBitmap.height - validTop)
            Bitmap.createBitmap(originalBitmap, validLeft, validTop, validWidth, validHeight)
        } else {
            originalBitmap
        }

        // 2. Matrix transformations (Rotate & Flip)
        val matrix = Matrix()
        if (rotateDegrees != 0f) {
            matrix.postRotate(rotateDegrees)
        }
        val scaleX = if (flipHorizontal) -1f else 1f
        val scaleY = if (flipVertical) -1f else 1f
        if (scaleX != 1f || scaleY != 1f) {
            matrix.postScale(scaleX, scaleY)
        }

        val transformedBitmap = if (!matrix.isIdentity) {
            Bitmap.createBitmap(cropped, 0, 0, cropped.width, cropped.height, matrix, true)
        } else {
            cropped
        }

        // 3. Color filtering (Brightness, Contrast, Saturation)
        val hasColorFilter = brightness != 0f || contrast != 1.0f || saturation != 1.0f
        val finalBitmap = if (hasColorFilter) {
            val result = Bitmap.createBitmap(
                transformedBitmap.width,
                transformedBitmap.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(result)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            val colorMatrix = ColorMatrix()

            // Saturation
            if (saturation != 1.0f) {
                colorMatrix.setSaturation(saturation)
            }

            // Contrast & Brightness
            if (contrast != 1.0f || brightness != 0f) {
                val scale = contrast
                val translate = brightness * 255f
                val cm = ColorMatrix(
                    floatArrayOf(
                        scale, 0f, 0f, 0f, translate,
                        0f, scale, 0f, 0f, translate,
                        0f, 0f, scale, 0f, translate,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                colorMatrix.postConcat(cm)
            }

            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
            canvas.drawBitmap(transformedBitmap, 0f, 0f, paint)
            result
        } else {
            transformedBitmap
        }

        // Save as a new copy (preserve original)
        val uniqueBase = UUID.randomUUID().toString()
        val destFile = File(mediaDirectory, "edited_$uniqueBase.jpg")
        FileOutputStream(destFile).use { out ->
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
            out.flush()
        }

        // Generate thumbnail
        val thumbFile = File(thumbnailDirectory, "edited_${uniqueBase}_thumb.jpg")
        generateImageThumbnail(destFile, thumbFile)

        // Recycle bitmaps
        if (finalBitmap != originalBitmap && finalBitmap != cropped && finalBitmap != transformedBitmap) {
            finalBitmap.recycle()
        }
        if (transformedBitmap != originalBitmap && transformedBitmap != cropped) {
            transformedBitmap.recycle()
        }
        if (cropped != originalBitmap) {
            cropped.recycle()
        }
        originalBitmap.recycle()

        MediaItem(
            id = 0,
            uri = Uri.fromFile(destFile).toString(),
            localPath = destFile.absolutePath,
            displayName = "Edited_${sourceItem.displayName}",
            mimeType = Constants.MIME_TYPE_JPEG,
            mediaType = MediaType.IMAGE,
            sizeBytes = destFile.length(),
            width = finalBitmap.width,
            height = finalBitmap.height,
            dateAdded = System.currentTimeMillis(),
            dateModified = System.currentTimeMillis(),
            isFavorite = false,
            thumbnailUri = if (thumbFile.exists()) Uri.fromFile(thumbFile).toString() else null
        )
    }

    suspend fun saveVideoCopy(sourceItem: MediaItem, customName: String? = null): MediaItem = withContext(Dispatchers.IO) {
        val originalFile = File(sourceItem.localPath)
        val uniqueBase = UUID.randomUUID().toString()
        val destFile = File(mediaDirectory, "copy_$uniqueBase.mp4")

        originalFile.inputStream().use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val thumbFile = File(thumbnailDirectory, "copy_${uniqueBase}_thumb.jpg")
        if (sourceItem.thumbnailUri != null) {
            try {
                val origThumb = File(Uri.parse(sourceItem.thumbnailUri).path ?: "")
                if (origThumb.exists()) {
                    origThumb.copyTo(thumbFile, overwrite = true)
                }
            } catch (_: Exception) {}
        }

        MediaItem(
            id = 0,
            uri = Uri.fromFile(destFile).toString(),
            localPath = destFile.absolutePath,
            displayName = customName ?: "Copy_${sourceItem.displayName}",
            mimeType = Constants.MIME_TYPE_MP4,
            mediaType = MediaType.VIDEO,
            sizeBytes = destFile.length(),
            width = sourceItem.width,
            height = sourceItem.height,
            durationMs = sourceItem.durationMs,
            dateAdded = System.currentTimeMillis(),
            dateModified = System.currentTimeMillis(),
            isFavorite = false,
            thumbnailUri = if (thumbFile.exists()) Uri.fromFile(thumbFile).toString() else null
        )
    }

    suspend fun exportToGallery(mediaItem: MediaItem): Uri = withContext(Dispatchers.IO) {
        val file = File(mediaItem.localPath)
        if (!file.exists()) throw IllegalStateException("Source media file does not exist")

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, mediaItem.displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mediaItem.mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    if (mediaItem.mediaType == MediaType.VIDEO) "Movies/LuminaMedia" else "Pictures/LuminaMedia"
                )
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val collectionUri = if (mediaItem.mediaType == MediaType.VIDEO) {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val resolver = context.contentResolver
        val insertedUri = resolver.insert(collectionUri, values)
            ?: throw IllegalStateException("Failed to create MediaStore entry")

        try {
            resolver.openOutputStream(insertedUri)?.use { out ->
                file.inputStream().use { input ->
                    input.copyTo(out)
                }
                out.flush()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(insertedUri, values, null, null)
            }
            insertedUri
        } catch (e: Exception) {
            resolver.delete(insertedUri, null, null)
            throw e
        }
    }

    suspend fun exportToSafUri(mediaItem: MediaItem, destUri: Uri): Boolean = withContext(Dispatchers.IO) {
        val file = File(mediaItem.localPath)
        val resolver = context.contentResolver
        resolver.openOutputStream(destUri)?.use { out ->
            file.inputStream().use { input ->
                input.copyTo(out)
            }
            out.flush()
            true
        } ?: false
    }

    fun getContentUriForSharing(mediaItem: MediaItem): Uri {
        val file = File(mediaItem.localPath)
        return FileProvider.getUriForFile(
            context,
            Constants.FILE_PROVIDER_AUTHORITY,
            file
        )
    }

    suspend fun deleteLocalFiles(mediaItem: MediaItem) = withContext(Dispatchers.IO) {
        try {
            val file = File(mediaItem.localPath)
            if (file.exists()) file.delete()
            if (mediaItem.thumbnailUri != null) {
                val thumbFile = File(Uri.parse(mediaItem.thumbnailUri).path ?: "")
                if (thumbFile.exists()) thumbFile.delete()
            }
        } catch (_: Exception) {}
    }

    private fun queryDisplayName(resolver: ContentResolver, uri: Uri): String? {
        return try {
            resolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    cursor.getString(nameIndex)
                } else null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun getExtension(filename: String, isVideo: Boolean): String {
        val dot = filename.lastIndexOf('.')
        return if (dot != -1 && dot < filename.length - 1) {
            filename.substring(dot + 1).lowercase()
        } else {
            if (isVideo) "mp4" else "jpg"
        }
    }

    private fun getMimeTypeFromExtension(path: String): String {
        return when {
            path.endsWith(".mp4", ignoreCase = true) -> "video/mp4"
            path.endsWith(".png", ignoreCase = true) -> "image/png"
            path.endsWith(".webp", ignoreCase = true) -> "image/webp"
            path.endsWith(".gif", ignoreCase = true) -> "image/gif"
            else -> "image/jpeg"
        }
    }
}
