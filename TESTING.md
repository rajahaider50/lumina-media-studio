# Automated Testing Specification — Lumina Media Studio

## Test Matrix

| Test Suite | File | Verifications |
|------------|------|---------------|
| **Validation Tests** | `ValidationTest.kt` | • Collection name non-empty check<br>• Collection name 50-character ceiling<br>• Search query trimming & whitespace handling |
| **Storage Calculation Tests** | `StorageCalculationTest.kt` | • Byte formatting logic (Bytes, KB, MB, GB boundaries)<br>• Device usage percentage computation from StatFs<br>• Zero-division safety |
| **Sort & Filter Tests** | `SortAndFilterTest.kt` | • Image and Video type filtering<br>• Favorite status filtering<br>• Large file filtering (>10MB)<br>• Multi-field sorting (Date DESC/ASC, Name ASC/DESC, Size DESC/ASC) |
| **Technical Formatters Tests** | `FormattersTest.kt` | • Video duration formatting (mm:ss padding)<br>• Null duration handling for images<br>• Resolution text parser (Width × Height) |
| **User Preferences & Result Tests** | `UserPreferencesTest.kt` | • Default and customized preferences validation<br>• Theme mode, grid density, and thumbnail quality checks<br>• AppResult.Success and AppResult.Error handling |

## Executing Tests

To run the full suite:
```bash
./gradlew test
```
All tests are implemented using standard JUnit 4 assertions and execute on JVM without requiring a connected physical device or emulator.
