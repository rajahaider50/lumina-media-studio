# Technical Architecture Specification — Lumina Media Studio

## 1. Architectural Philosophy

Lumina Media Studio follows strict Clean Architecture and reactive MVVM principles:

- **Presentation Layer**: Composable functions consume immutable `UiState` via Kotlin Coroutines `StateFlow` and emit user intent events. Composable functions contain zero database or file I/O operations.
- **Domain Layer**: Contains business entities (`MediaItem`, `CollectionItem`, `ActivityItem`, `StorageInfo`) and single-responsibility use cases (`ImportMediaUseCase`, `EditImageUseCase`, `DeleteMediaUseCase`, etc.) encapsulating validation and audit logging.
- **Data Layer**: Implements repository interfaces (`MediaRepositoryImpl`, `CollectionRepositoryImpl`, etc.), coordinating between the local SQLite Room database, Jetpack DataStore, and Android platform media managers.
- **Core Layer**: Manages lower-level system capabilities such as `StorageManager` (StatFs, directory traversal), `MediaManager` (ContentResolver streams, thumbnails, ColorMatrix transformations), and `PermissionManager` (version-specific Android permissions).

---

## 2. Room Database Schema

The database (`lumina_media.db`) comprises 5 normalized entities with optimized query indices:

### `media`
- `id` (Long, PrimaryKey, AutoGenerate)
- `uri` (String): Content or file URI
- `localPath` (String): Private app-sandboxed absolute path
- `displayName` (String) [Indexed]
- `mimeType` (String) [Indexed]
- `mediaType` (String: "IMAGE" | "VIDEO") [Indexed]
- `sizeBytes` (Long)
- `width` (Int), `height` (Int)
- `durationMs` (Long, Nullable)
- `dateAdded` (Long) [Indexed]
- `dateModified` (Long)
- `isFavorite` (Boolean) [Indexed]
- `thumbnailUri` (String, Nullable)
- `hash` (String, Nullable, SHA-256)
- `createdAt` (Long), `updatedAt` (Long)

### `collections`
- `id` (Long, PrimaryKey, AutoGenerate)
- `name` (String, Unique Index)
- `description` (String)
- `coverUri` (String, Nullable)
- `createdAt` (Long), `updatedAt` (Long)

### `collection_media_cross_ref`
- `collectionId` (Long) [ForeignKey → collections.id, CASCADE]
- `mediaId` (Long) [ForeignKey → media.id, CASCADE]
- `addedAt` (Long)
- `sortOrder` (Int)
- PrimaryKey: `[collectionId, mediaId]`

### `activity`
- `id` (Long, PrimaryKey, AutoGenerate)
- `type` (String: IMPORTED, OPENED, CREATED_COLLECTION, etc.)
- `mediaId` (Long, Nullable)
- `collectionId` (Long, Nullable)
- `description` (String)
- `timestamp` (Long) [Indexed]

### `recent_searches`
- `id` (Long, PrimaryKey, AutoGenerate)
- `query` (String, Unique Index)
- `timestamp` (Long)

---

## 3. Storage & Sandboxing Strategy

1. **Private App Storage**:
   - Location: `context.filesDir/media/`
   - Benefits: Zero external permission needed to access internal media files; Android automatically protects app-private storage from other apps.
2. **Thumbnails**:
   - Location: `context.filesDir/thumbnails/`
   - Generated during import using downsampled decoding (`inSampleSize`) to guarantee smooth 60fps Compose scrolling with low memory footprint.
3. **Sharing & Exporting**:
   - Implements `androidx.core.content.FileProvider` mapping `res/xml/file_paths.xml` to prevent raw `file://` URI leakage, ensuring Android 7.0+ strict mode compliance.
   - Gallery exports utilize `MediaStore.Images.Media` and `MediaStore.Video.Media` with `RELATIVE_PATH` targeting `Pictures/LuminaMedia` and `Movies/LuminaMedia`.
