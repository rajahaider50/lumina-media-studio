package com.example.premiumapp.core.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import com.example.premiumapp.core.common.AppError
import com.example.premiumapp.core.common.AppResult
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.model.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.UUID

data class ImageEditParams(
    val rotationDegrees: Float = 0f,
    val flipHorizontal: Boolean = false,
    val flipVertical: Boolean = false,
    val brightness: Float = 0f, // -100 to 100
    val contrast: Float = 1f,   // 0.5 to 2.0
    val saturation: Float = 1f, // 0 to 2.0
    val cropRect: androidx.compose.ui.geometry.Rect? = null
)

class MediaEditor(
    private val context: Context,
    private val thumbnailGenerator: ThumbnailGenerator
) {

    private val mediaStorageDir: File by lazy {
        File(context.filesDir, "media").apply { if (!exists()) mkdirs() }
    }

    suspend fun applyImageEditsAndSaveCopy(
        original: MediaItem,
        params: ImageEditParams
    ): AppResult<MediaItem> = withContext(Dispatchers.IO) {
        try {
            val sourceFile = original.localPath?.let { File(it) }
            if (sourceFile == null || !sourceFile.exists()) {
                return@withContext AppResult.Error(AppError.MediaNotFound("Source image file not found"))
            }

            var bitmap = BitmapFactory.decodeFile(sourceFile.absolutePath)
                ?: return@withContext AppResult.Error(AppError.MediaCorrupted("Failed to decode source image"))

            // Apply rotation and flips
            val matrix = Matrix()
            if (params.rotationDegrees != 0f) {
                matrix.postRotate(params.rotationDegrees)
            }
            val scaleX = if (params.flipHorizontal) -1f else 1f
            val scaleY = if (params.flipVertical) -1f else 1f
            if (scaleX != 1f || scaleY != 1f) {
                matrix.postScale(scaleX, scaleY)
            }

            var transformedBitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
            if (transformedBitmap != bitmap) {
                bitmap.recycle()
                bitmap = transformedBitmap
            }

            // Apply color adjustments (Brightness, Contrast, Saturation)
            if (params.brightness != 0f || params.contrast != 1f || params.saturation != 1f) {
                val adjustedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config ?: Bitmap.Config.ARGB_8888)
                val canvas = Canvas(adjustedBitmap)
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)

                val cm = ColorMatrix()

                // Saturation
                if (params.saturation != 1f) {
                    val satMatrix = ColorMatrix()
                    satMatrix.setSaturation(params.saturation)
                    cm.postConcat(satMatrix)
                }

                // Scale (contrast) and translate (brightness)
                val scale = params.contrast
                val translate = params.brightness
                val contrastMatrix = ColorMatrix(
                    floatArrayOf(
                        scale, 0f, 0f, 0f, translate,
                        0f, scale, 0f, 0f, translate,
                        0f, 0f, scale, 0f, translate,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                cm.postConcat(contrastMatrix)

                paint.colorFilter = ColorMatrixColorFilter(cm)
                canvas.drawBitmap(bitmap, 0f, 0f, paint)

                bitmap.recycle()
                bitmap = adjustedBitmap
            }

            // Save new copy to disk
            val newFileName = "edited_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg"
            val targetFile = File(mediaStorageDir, newFileName)

            FileOutputStream(targetFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
            }

            val targetUri = Uri.fromFile(targetFile)
            val thumbUri = thumbnailGenerator.generateImageThumbnail(targetUri)

            val editedItem = MediaItem(
                uri = targetUri.toString(),
                localPath = targetFile.absolutePath,
                displayName = "Edited_${original.displayName}",
                mimeType = "image/jpeg",
                mediaType = MediaType.IMAGE,
                sizeBytes = targetFile.length(),
                width = bitmap.width,
                height = bitmap.height,
                durationMs = null,
                dateAdded = System.currentTimeMillis(),
                dateModified = targetFile.lastModified(),
                isFavorite = false,
                thumbnailUri = thumbUri
            )

            bitmap.recycle()
            AppResult.Success(editedItem)
        } catch (e: Exception) {
            e.printStackTrace()
            AppResult.Error(AppError.ExportFailed("Failed to edit image: ${e.localizedMessage}", e))
        }
    }

    suspend fun trimVideoAndSaveCopy(
        original: MediaItem,
        startMs: Long,
        endMs: Long
    ): AppResult<MediaItem> = withContext(Dispatchers.IO) {
        try {
            val sourceFile = original.localPath?.let { File(it) }
            if (sourceFile == null || !sourceFile.exists()) {
                return@withContext AppResult.Error(AppError.MediaNotFound("Source video file not found"))
            }

            val newFileName = "trimmed_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.mp4"
            val targetFile = File(mediaStorageDir, newFileName)

            val extractor = MediaExtractor()
            extractor.setDataSource(sourceFile.absolutePath)

            val muxer = MediaMuxer(targetFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            val trackCount = extractor.trackCount
            val trackMap = HashMap<Int, Int>()

            for (i in 0 until trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME) ?: ""
                if (mime.startsWith("video/") || mime.startsWith("audio/")) {
                    extractor.selectTrack(i)
                    val muxerTrack = muxer.addTrack(format)
                    trackMap[i] = muxerTrack
                }
            }

            muxer.start()

            val startUs = startMs * 1000L
            val endUs = endMs * 1000L
            extractor.seekTo(startUs, MediaExtractor.SEEK_TO_CLOSEST_SYNC)

            val maxBufferSize = 1024 * 1024
            val buffer = ByteBuffer.allocate(maxBufferSize)
            val bufferInfo = MediaCodec.BufferInfo()

            while (true) {
                val trackIndex = extractor.sampleTrackIndex
                if (trackIndex < 0) break

                val sampleTime = extractor.sampleTime
                if (sampleTime > endUs) break

                if (sampleTime >= startUs) {
                    bufferInfo.offset = 0
                    bufferInfo.size = extractor.readSampleData(buffer, 0)
                    bufferInfo.presentationTimeUs = sampleTime - startUs
                    bufferInfo.flags = extractor.sampleFlags

                    val muxerTrack = trackMap[trackIndex]
                    if (muxerTrack != null && bufferInfo.size > 0) {
                        muxer.writeSampleData(muxerTrack, buffer, bufferInfo)
                    }
                }
                extractor.advance()
            }

            muxer.stop()
            muxer.release()
            extractor.release()

            val targetUri = Uri.fromFile(targetFile)
            val thumbUri = thumbnailGenerator.generateVideoThumbnail(targetUri)

            val trimmedItem = MediaItem(
                uri = targetUri.toString(),
                localPath = targetFile.absolutePath,
                displayName = "Trimmed_${original.displayName}",
                mimeType = "video/mp4",
                mediaType = MediaType.VIDEO,
                sizeBytes = targetFile.length(),
                width = original.width,
                height = original.height,
                durationMs = (endMs - startMs).coerceAtLeast(0),
                dateAdded = System.currentTimeMillis(),
                dateModified = targetFile.lastModified(),
                isFavorite = false,
                thumbnailUri = thumbUri
            )

            AppResult.Success(trimmedItem)
        } catch (e: Exception) {
            e.printStackTrace()
            AppResult.Error(AppError.ExportFailed("Failed to trim video: ${e.localizedMessage}", e))
        }
    }
}
