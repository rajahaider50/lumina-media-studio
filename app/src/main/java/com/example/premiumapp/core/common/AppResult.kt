package com.example.premiumapp.core.common

sealed interface AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>
    data class Error(val error: AppError) : AppResult<Nothing>
}

sealed class AppError(open val message: String, open val cause: Throwable? = null) {
    data class MediaNotFound(override val message: String = "Media not found", override val cause: Throwable? = null) : AppError(message, cause)
    data class MediaCorrupted(override val message: String = "Media file is corrupted or unsupported", override val cause: Throwable? = null) : AppError(message, cause)
    data class StorageFull(override val message: String = "Storage is full", override val cause: Throwable? = null) : AppError(message, cause)
    data class PermissionDenied(override val message: String = "Required permission was denied", override val cause: Throwable? = null) : AppError(message, cause)
    data class ExportFailed(override val message: String = "Export operation failed", override val cause: Throwable? = null) : AppError(message, cause)
    data class ImportFailed(override val message: String = "Import operation failed", override val cause: Throwable? = null) : AppError(message, cause)
    data class Unknown(override val message: String = "An unexpected error occurred", override val cause: Throwable? = null) : AppError(message, cause)
}
