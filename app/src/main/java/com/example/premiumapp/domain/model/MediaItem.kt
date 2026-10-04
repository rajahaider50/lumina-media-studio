package com.example.premiumapp.domain.model

data class MediaItem(
    val id: Long = 0,
    val uri: String,
    val localPath: String,
    val displayName: String,
    val mimeType: String,
    val mediaType: MediaType,
    val sizeBytes: Long,
    val width: Int = 0,
    val height: Int = 0,
    val durationMs: Long? = null,
    val dateAdded: Long = System.currentTimeMillis(),
    val dateModified: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val thumbnailUri: String? = null,
    val hash: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isVideo: Boolean get() = mediaType == MediaType.VIDEO
    val isImage: Boolean get() = mediaType == MediaType.IMAGE

    val formattedSize: String
        get() {
            val kb = sizeBytes / 1024.0
            val mb = kb / 1024.0
            val gb = mb / 1024.0
            return when {
                gb >= 1.0 -> String.format("%.2f GB", gb)
                mb >= 1.0 -> String.format("%.1f MB", mb)
                kb >= 1.0 -> String.format("%.0f KB", kb)
                else -> "$sizeBytes B"
            }
        }

    val formattedDuration: String?
        get() {
            if (durationMs == null || durationMs <= 0) return null
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }

    val resolutionText: String
        get() = if (width > 0 && height > 0) "${width} × ${height}" else "Unknown resolution"
}
