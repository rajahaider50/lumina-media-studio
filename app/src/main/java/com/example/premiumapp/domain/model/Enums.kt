package com.example.premiumapp.domain.model

enum class MediaType {
    IMAGE,
    VIDEO
}

enum class ThemeMode(val title: String) {
    SYSTEM("System Default"),
    LIGHT("Light"),
    DARK("Dark"),
    AMOLED("AMOLED Dark")
}

enum class GridDensity(val columnsPhone: Int, val columnsTablet: Int, val title: String) {
    COMPACT(4, 6, "Compact"),
    STANDARD(3, 4, "Standard"),
    COMFORTABLE(2, 3, "Comfortable")
}

enum class ThumbnailQuality(val maxDimension: Int, val title: String) {
    LOW(256, "Low (Faster, Low Memory)"),
    MEDIUM(512, "Medium (Balanced)"),
    HIGH(1024, "High (Crisp)")
}

enum class SortMode(val title: String) {
    NEWEST("Newest First"),
    OLDEST("Oldest First"),
    NAME_AZ("Name (A to Z)"),
    NAME_ZA("Name (Z to A)"),
    LARGEST("Largest First"),
    SMALLEST("Smallest First")
}

enum class SortOption(val title: String) {
    DATE_DESC("Newest First"),
    DATE_ASC("Oldest First"),
    NAME_ASC("Name (A to Z)"),
    NAME_DESC("Name (Z to A)"),
    SIZE_DESC("Largest First"),
    SIZE_ASC("Smallest First")
}

enum class FilterOption(val title: String) {
    ALL("All Media"),
    IMAGES("Photos"),
    VIDEOS("Videos"),
    FAVORITES("Favorites"),
    RECENT("Recently Added"),
    LARGE_FILES("Large Files (>10MB)")
}

enum class ActivityType(val label: String) {
    IMPORTED("Imported Media"),
    OPENED("Viewed Media"),
    CREATED_COLLECTION("Created Collection"),
    ADDED_TO_COLLECTION("Added to Collection"),
    EDITED("Edited Media"),
    EXPORTED("Exported Media"),
    DELETED("Deleted Media"),
    FAVORITED("Favorited Media"),
    UNFAVORITED("Unfavorited Media")
}

enum class PermissionType(val title: String, val rationale: String) {
    CAMERA(
        "Camera",
        "Required to take new photos and record videos directly into Lumina Studio."
    ),
    PHOTOS(
        "Photos & Images",
        "Allows selecting and importing existing photos from your device storage."
    ),
    VIDEOS(
        "Videos",
        "Allows selecting and importing video clips for viewing, trimming, and offline organization."
    ),
    NOTIFICATIONS(
        "Notifications",
        "Notifies you when background operations like batch imports or exports complete."
    )
}

enum class PermissionStatus {
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED
}
