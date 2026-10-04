# Lumina Media Studio — Premium Native Android Application

Lumina Media Studio is a genuine, high-performance, 100% offline-first native Android application built with Kotlin, Jetpack Compose, Material 3, and Clean Architecture.

Designed to deliver the polish, responsiveness, generous typography, and refined aesthetic of high-end mobile experiences while honoring native Android conventions, lifecycle semantics, and permissions.

---

## 25 Complete Functional Pages

| # | Page | Route | Description |
|---|------|-------|-------------|
| 01 | **Splash Screen** | `splash` | Animated vector branding, local database initialization, and route dispatching. |
| 02 | **Onboarding** | `onboarding` | 4-panel interactive onboarding with persistent DataStore state. |
| 03 | **Home Dashboard** | `home` | Dynamic greeting, live device storage breakdown widget, quick actions hub, and recent media carousels. |
| 04 | **Explore** | `explore` | Filter chips (Photos, Videos, Favorites, Large Files), sorting dropdown, and adaptive grid. |
| 05 | **Search** | `search` | 300ms debounced Room queries across filenames and MIME types, with persistent search history. |
| 06 | **Collections** | `collections` | Albums grid with dynamic auto-covers, media counts, rename dialogs, and deletion flows. |
| 07 | **Create Collection** | `create_collection` | Validated collection name, optional description, and cover picker from library. |
| 08 | **Collection Details** | `collection_details/{id}` | Album header, media grid, and batch add/remove modal. |
| 09 | **Import Media** | `import_media` | Dual import engines: Android Photo Picker and Storage Access Framework (SAF) with live progress. |
| 10 | **Media Details** | `media_details/{id}` | Technical metadata (dimensions, size, duration, date), album tagging, and quick actions. |
| 11 | **Media Viewer** | `media_viewer/{id}` | Image viewer with pinch-to-zoom, pan, double-tap zoom; video player with seek bar and mute. |
| 12 | **Editor** | `editor/{id}` | Rotate (90°), flip horizontal/vertical, brightness, contrast, and saturation adjustments with non-destructive copy saving. |
| 13 | **Favorites** | `favorites` | Instant favorites view with grid/list switcher and quick unfavorite actions. |
| 14 | **Recent Activity** | `activity` | Activity audit log (Import, Edit, Album, Delete, Favorite) with retention control. |
| 15 | **Profile** | `profile` | Local offline profile with editable creator name and real library statistics. |
| 16 | **Settings** | `settings` | Grouped settings hub for themes, storage, permissions, privacy, and history cleanup. |
| 17 | **Appearance** | `appearance` | System, Light, Dark, and AMOLED Dark themes; Dynamic Color; Reduced Motion toggle; Grid Density; Thumbnail Quality. |
| 18 | **Permissions Center** | `permissions` | Android version-aware status for Camera, Photos, Videos, and Notifications with direct Settings intent. |
| 19 | **Storage Center** | `storage` | Real internal device and app storage statistics via `StatFs`, cache cleaner, and large files inspector (>10MB). |
| 20 | **Notifications Center** | `notifications` | Alert preferences for imports, exports, and storage warnings, plus test notification emitter. |
| 21 | **Media Selection Mode** | `selection` | Batch actions: Favorite, Add to Album, Share (`ACTION_SEND_MULTIPLE`), Export, and Delete. |
| 22 | **Share & Export** | `share_export/{ids}` | Android Sharesheet with secure `FileProvider`, gallery export via `MediaStore`, and custom SAF file creation. |
| 23 | **Help & Privacy** | `help_privacy` | Expandable guides on sandbox copying, offline database, cache lifecycle, and support mail. |
| 24 | **About** | `about` | Dynamic build metadata, architecture overview, open source licenses, and legal terms. |
| 25 | **State & Error Hub** | `error_hub` | Interactive testbed for all reusable states: Offline, Permission Denied, Missing File, Storage Warning, Import/Export Failure. |

---

## Technology Stack

- **Platform**: Android Native (Kotlin)
- **UI Framework**: Jetpack Compose + Material 3
- **Architecture**: Clean Architecture + MVVM + Repository Pattern
- **Persistence**: Room SQLite Database (v1) with transactional relations and indices
- **Preferences**: Jetpack DataStore Preferences
- **Background Tasks**: Android WorkManager
- **Navigation**: Navigation Compose with typed arguments and animated transitions
- **Image & Video Loading**: Coil 2.5 with `VideoFrameDecoder`
- **System Integration**:
  - Android Photo Picker (`ActivityResultContracts.PickMultipleVisualMedia`)
  - Storage Access Framework (`ActivityResultContracts.OpenMultipleDocuments`, `CreateDocument`)
  - MediaStore APIs (`MediaStore.Images`, `MediaStore.Video`)
  - FileProvider (`androidx.core.content.FileProvider`) for secure content URI sharing

---

## Architecture Flow

```
UI (Jetpack Compose Screens & Reusable Components)
       ↓
ViewModel (Unidirectional StateFlow & User Events)
       ↓
Domain Use Cases (Business rules, validations, storage checks)
       ↓
Repository Layer (MediaRepository, CollectionRepository, ActivityRepository, PreferencesRepository)
       ↓
Local Data Sources (Room AppDatabase + Jetpack DataStore + MediaManager + StorageManager)
```

---

## Automated Test Suite

Unit tests located in `app/src/test/java/com/example/premiumapp/`:
- `ValidationTest.kt`: Collection name length constraints, duplicate checks, and search trimming.
- `StorageCalculationTest.kt`: Formatting bytes (B, KB, MB, GB) and device storage utilization percentages.
- `SortAndFilterTest.kt`: Multi-criteria sorting (Date, Name, Size) and category filtering (Images, Videos, Favorites, Large Files).
- `FormattersTest.kt`: Duration formatting (mm:ss), resolution parsing, and fallback strings.

---

## Building and Running

1. **Prerequisites**:
   - JDK 17
   - Android SDK 34 (Android 14)
2. **Build**:
   ```bash
   ./gradlew assembleDebug
   ```
3. **Run Tests**:
   ```bash
   ./gradlew test
   ```
4. **Install on device / emulator**:
   ```bash
   ./gradlew installDebug
   ```
