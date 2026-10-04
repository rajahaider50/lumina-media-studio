# Developer Setup Guide — Lumina Media Studio

## Environment Requirements

- **JDK**: Version 17 (e.g. OpenJDK 17 or Eclipse Temurin 17)
- **Android SDK**: Build-Tools 34.0.0, Platforms android-34
- **Gradle**: 8.1.1 (or modern Gradle wrapper)
- **Memory**: Recommended minimum 4GB RAM allocated to Gradle daemon

## Configuration

In `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.enableJetifier=false
kotlin.code.style=official
android.nonTransitiveRClass=true
```

## Build Targets

### 1. Compile and Assemble Debug APK
```bash
./gradlew assembleDebug
```
Output artifact: `app/build/outputs/apk/debug/app-debug.apk`

### 2. Run Unit Tests
```bash
./gradlew test
```

### 3. Clean Project
```bash
./gradlew clean
```
