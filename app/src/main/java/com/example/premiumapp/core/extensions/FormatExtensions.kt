package com.example.premiumapp.core.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun Long.formatFileSize(): String {
    if (this <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(this.toDouble()) / Math.log10(1024.0)).toInt()
    val clampedGroup = digitGroups.coerceIn(0, units.size - 1)
    val value = this / Math.pow(1024.0, clampedGroup.toDouble())
    return String.format(Locale.getDefault(), "%.1f %s", value, units[clampedGroup])
}

fun Long.formatDuration(): String {
    if (this <= 0) return "0:00"
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60
    return if (hours > 0) {
        String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }
}

fun Long.formatDate(pattern: String = "MMM dd, yyyy"): String {
    return try {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.format(Date(this))
    } catch (e: Exception) {
        ""
    }
}

fun Long.formatDateTime(): String {
    return formatDate("MMM dd, yyyy • hh:mm a")
}

fun formatFileSize(bytes: Long): String = bytes.formatFileSize()
fun formatDuration(ms: Long): String = ms.formatDuration()
fun formatDate(timestamp: Long, pattern: String = "MMM dd, yyyy"): String = timestamp.formatDate(pattern)
fun formatDateTime(timestamp: Long): String = timestamp.formatDateTime()

fun formatDimensions(width: Int?, height: Int?): String {
    return if (width != null && height != null && width > 0 && height > 0) {
        "${width} × ${height}"
    } else {
        "Unknown Dimensions"
    }
}
