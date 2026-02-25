# moon-i18n-kmp

Hybrid internationalization system for Kotlin Multiplatform with bundled and on-demand translations.

## Features

- **Locale management** abstraction (system locale detection, app locale switching)
- **Translation provider** with key-based string lookup, parameterized strings, and plurals
- **Hybrid source strategy** (bundled first, then remote with download-on-demand)
- **Fallback chain** (current locale -> fallback locale -> raw key)
- **NoOp implementations** for testing

## Installation

```kotlin
// includeBuild (local development)
implementation("com.moondeveloper:moon-i18n-kmp")

// Maven Central (coming soon)
implementation("com.moondeveloper:moon-i18n-kmp:1.0.0")
```

## Quick Start

```kotlin
val localeManager: LocaleManager = get()
val translations: TranslationProvider = get()

// Get system locale
val systemLocale = localeManager.getSystemLocale()
println(systemLocale.tag) // "ko-KR"

// Switch app locale
localeManager.setAppLocale(AppLocale("en", "US"))

// Translate strings
val greeting = translations.getString("hello")
val welcome = translations.getString("welcome_user", "Alice") // "Welcome, Alice!"
val items = translations.getPlural("item_count", 5) // "5 items"

// Set up hybrid provider with bundled + remote sources
val provider = HybridTranslationProvider(
    bundledSource = myBundledSource,
    remoteSource = myRemoteSource,
    fallbackLocale = AppLocale("en")
)
provider.initialize()
provider.setLocale(AppLocale("ko"))
```

## API Overview

| Type | Description |
|------|-------------|
| `LocaleManager` | System locale detection and app locale switching |
| `AppLocale` | Locale data (languageCode, countryCode, displayName, tag) |
| `TranslationProvider` | String lookup with parameters and plurals |
| `HybridTranslationProvider` | Bundled-first, remote-fallback implementation |
| `TranslationSource` | Base source interface (getTranslations, isAvailable) |
| `BundledTranslationSource` | Marker interface for app-bundled translations |
| `RemoteTranslationSource` | Download-on-demand translations with cache |
| `NoOpLocaleManager` | No-op locale manager |
| `NoOpTranslationProvider` | Returns raw keys as values |

## Platform Support

| Platform | Status |
|----------|--------|
| Android | Supported |
| iOS | Supported |
| Desktop (JVM) | Supported |

## License

Apache License 2.0
