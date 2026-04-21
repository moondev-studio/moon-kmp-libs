# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0-SNAPSHOT] — Unreleased

### BREAKING CHANGES

#### moon-billing-kmp

- **`PurchaseResult.Success`**: added required `receipt: String` parameter (3rd positional
  argument). Previous signature `Success(productId, purchaseToken)` has been removed.
  - Android callers: pass `Purchase.originalJson` (or empty string for restore/pending paths).
  - iOS callers: pass `Transaction.jwsRepresentation`.
  - Fake/NoOp engines: pass any non-null string (empty is accepted).
- **`IOSBillingBridge.requestPurchase`**: `onSuccess` callback signature changed from
  `() -> Unit` to `(receipt: String, transactionId: String) -> Unit`. Consumers receive the
  StoreKit 2 receipt and transaction identifier directly.
- **`IOSBillingBridge.completePurchase`**: signature expanded from 3 parameters
  `(success, cancelled, error)` to 5 parameters `(success, cancelled, error, receipt,
  transactionId)`. Swift callers must forward `transaction.jwsRepresentation` and
  `String(transaction.id)` into the new required parameters. Use empty strings for the
  cancel/error branches.
- **`StoreKitBillingEngine.purchase`**: now populates `PurchaseResult.Success.purchaseToken`
  with the real StoreKit 2 `Transaction.id` (previously hardcoded to `"ios_purchase"`) and
  `receipt` with the `jwsRepresentation`.

#### Why

Server-side receipt verification (e.g. Splitly `PaywallViewModel.verifyOnServer`) requires
both the receipt payload and a transaction identifier. In v1.x the iOS callback chain dropped
these values at the bridge, forcing consumers to pass blank receipts to their backend. The
5-parameter `completePurchase` propagates them end-to-end without any Apple SDK dependency
leaking into common/iOS Kotlin code.

See `moon-billing-kmp/README.md` (Migration Guide: v1 → v2) for exact before/after snippets
for both Kotlin and Swift call sites.

## [1.0.0] — 2026-04-04

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
