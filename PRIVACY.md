# Privacy & Data Sovereignty Policy — Lumina Media Studio

## 1. Zero Telemetry & Local Execution

Lumina Media Studio is built on an absolute local-first architecture:
- **No Cloud Services**: The application does not communicate with external servers or cloud storage providers.
- **No Analytics or Trackers**: No third-party tracking SDKs, telemetry libraries, or advertising frameworks are embedded in the software.
- **Local Storage Only**: Media files, album metadata, activity history, and user settings reside exclusively within the application's sandboxed storage (`/data/user/0/com.example.premiumapp/`).

## 2. Android Runtime Permissions

Permissions are requested on a strictly feature-driven, just-in-time basis:
- `android.permission.CAMERA`: Requested only if the user chooses to capture live media.
- `android.permission.READ_MEDIA_IMAGES` / `READ_MEDIA_VIDEO`: Utilized on supported Android versions when browsing device galleries. The application prioritizes the Android Photo Picker (`PickVisualMedia`) to minimize broad storage access.
- `android.permission.POST_NOTIFICATIONS`: Utilized solely for user-facing local alerts upon completing background batch tasks (import/export).

## 3. Data Deletion

The user possesses complete ownership of their data:
- Deleting an album from Lumina removes only the album classification; media items remain untouched.
- Deleting a media file permanently purges the local sandboxed copy and generated thumbnails.
- The 'Clean Cache' utility in the Storage Center deletes disposable thumbnails without impacting original media files.
