# moon-kmp-libs

MoonDeveloper OSS Kotlin Multiplatform Libraries.

> Under development -- not yet published to Maven Central.

## Modules

| Module | Description | Status |
|--------|------------|--------|
| moon-analytics-kmp | Analytics & Crash reporting interfaces | WIP |
| moon-auth-kmp | Authentication interfaces | Planned |
| moon-billing-kmp | In-app purchase interfaces | Planned |
| moon-sync-kmp | Offline-first sync engine | Planned |
| moon-i18n-kmp | Internationalization | Planned |
| moon-ui-kmp | Compose Multiplatform UI kit | Planned |

## Architecture

These libraries provide **dependency-free interfaces**. No Firebase, Google Play, or Apple SDK dependencies.
Concrete implementations live in each app's library layer.
