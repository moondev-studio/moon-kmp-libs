# Moon KMP Libraries

A collection of Kotlin Multiplatform libraries for building cross-platform mobile apps.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-blue.svg)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose_Multiplatform-1.10.1-green.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![License](https://img.shields.io/badge/License-Apache_2.0-orange.svg)](LICENSE)

## Modules

| Module | Description | Platforms |
|--------|-------------|-----------|
| [moon-analytics-kmp](moon-analytics-kmp/) | Analytics & crash reporting abstraction | Android, iOS, Desktop |
| [moon-auth-kmp](moon-auth-kmp/) | Authentication provider abstraction | Android, iOS, Desktop |
| [moon-billing-kmp](moon-billing-kmp/) | In-app purchase & premium management | Android, iOS, Desktop |
| [moon-sync-kmp](moon-sync-kmp/) | Offline-first sync engine with conflict resolution | Android, iOS, Desktop |
| [moon-ui-kmp](moon-ui-kmp/) | Adaptive UI components for Compose Multiplatform | Android, iOS, Desktop |
| [moon-i18n-kmp](moon-i18n-kmp/) | Hybrid i18n (bundled + on-demand translation) | Android, iOS, Desktop |
| [moon-ocr-kmp](moon-ocr-kmp/) | OCR & receipt parsing abstraction | Android, iOS, Desktop |

## Architecture

These libraries provide **dependency-free interfaces**. No Firebase, Google Play, or Apple SDK dependencies in the OSS layer.

```
App (composeApp)
 └── App Library (splitly-auth, splitly-billing, ...)    ← Platform SDKs here
      └── OSS Library (moon-auth-kmp, moon-billing-kmp, ...) ← Pure Kotlin interfaces
```

Each module ships with `NoOp` implementations for testing and unsupported platforms.

## Quick Start

### Local Development (includeBuild)

```kotlin
// settings.gradle.kts
includeBuild("../moon-kmp-libs")

// module build.gradle.kts
dependencies {
    implementation("com.moondeveloper:moon-analytics-kmp")
}
```

### Maven Central (coming soon)

```kotlin
dependencies {
    implementation("com.moondeveloper:moon-analytics-kmp:1.0.0")
}
```

## Requirements

- Kotlin 2.3.0+
- AGP 8.13.2+
- Gradle 8.14+
- Android: compileSdk 36, minSdk 24
- Compose Multiplatform 1.10.1+ (moon-ui-kmp only)

## Building

```bash
# Clone
git clone https://github.com/sun941003/moon-kmp-libs.git
cd moon-kmp-libs

# Create local.properties with Android SDK path
echo "sdk.dir=/path/to/android/sdk" > local.properties

# Build all modules
./gradlew build

# Run tests (Desktop JVM)
./gradlew desktopTest
```

## License

Apache License 2.0 - see [LICENSE](LICENSE)
