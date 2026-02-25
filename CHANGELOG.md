# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- **moon-analytics-kmp**: Analytics event tracking, crash reporting, screen/action/performance/conversion trackers, composite pattern, sampling
- **moon-auth-kmp**: Authentication abstraction (email, Google, Apple), typed AuthResult/AuthException, Fake/NoOp providers
- **moon-billing-kmp**: Billing engine abstraction (products, purchase, restore), premium tier management, usage limit checking, Fake/NoOp implementations
- **moon-sync-kmp**: Offline-first sync engine with queue, conflict resolution (server-wins), network monitoring, DefaultSyncManager with retry
- **moon-ui-kmp**: Window size classes, adaptive layouts (AdaptiveContentWidth, TwoPane, ListDetailLayout), MoonThemeTokens interface
- **moon-i18n-kmp**: Locale management, hybrid translation provider (bundled + remote), plural support
- **moon-ocr-kmp**: OCR engine abstraction, receipt parser interface, text block/bounding box models
- KDoc documentation for all public APIs
- Dokka V2 HTML documentation generation
- Maven Central publish configuration (vanniktech plugin)
- Convention plugins: moon.kmp.library, moon.compose.library, moon.publish
