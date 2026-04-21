# moon-billing-kmp

Platform-agnostic in-app purchase and premium management for Kotlin Multiplatform.

## Features

- **Billing engine** abstraction (query products, purchase, restore, consume)
- **Premium state management** with tier support (Free, Premium, Premium+)
- **Usage limit checking** with feature-level granularity
- **Reactive state** via `Flow<PurchaseState>` and `Flow<PremiumTier>`
- **NoOp and Fake** implementations for testing

## Installation

```kotlin
// includeBuild (local development)
implementation("com.moondeveloper:moon-billing-kmp")

// Maven Central (coming soon)
implementation("com.moondeveloper:moon-billing-kmp:1.0.0")
```

## Quick Start

```kotlin
val billing: BillingEngine = get()
val premiumManager: PremiumStateManager = get()
val limitChecker: UsageLimitChecker = get()

// Initialize and query products
billing.initialize()
val products = billing.queryProducts(listOf("premium_monthly", "premium_yearly"))

// Purchase
when (val result = billing.purchase(products.first())) {
    is PurchaseResult.Success -> println("Purchased: ${result.productId}")
    is PurchaseResult.Cancelled -> println("User cancelled")
    is PurchaseResult.AlreadyOwned -> println("Already owned")
    is PurchaseResult.Error -> println("Error: ${result.exception.message}")
}

// Check premium status
premiumManager.premiumTier.collect { tier ->
    when (tier) {
        PremiumTier.FREE -> showAds()
        PremiumTier.PREMIUM -> hideAds()
        PremiumTier.PREMIUM_PLUS -> unlockAllFeatures()
    }
}

// Check usage limits
when (val check = limitChecker.canUse("settlement")) {
    is LimitCheckResult.Allowed -> proceed()
    is LimitCheckResult.LimitReached -> showUpgrade("${check.used}/${check.limit} used")
    is LimitCheckResult.PremiumRequired -> showPaywall()
}
```

## API Overview

| Type | Description |
|------|-------------|
| `BillingEngine` | Core billing interface (products, purchase, restore) |
| `Product` | Product data (id, name, price, type) |
| `ProductType` | Enum: ONE_TIME, CONSUMABLE, SUBSCRIPTION |
| `PurchaseResult` | Sealed class: Success, Cancelled, Error, AlreadyOwned |
| `PurchaseState` | Sealed class: Idle, Loading, Purchased, Error |
| `PremiumStateManager` | Premium tier management and promo codes |
| `PremiumTier` | Enum: FREE, PREMIUM, PREMIUM_PLUS |
| `UsageLimitChecker` | Feature usage limit enforcement |
| `LimitCheckResult` | Sealed class: Allowed, LimitReached, PremiumRequired |
| `NoOpBillingEngine` | No-op billing (all operations return empty/success) |
| `NoOpPremiumStateManager` | Always returns FREE tier |
| `NoOpUsageLimitChecker` | Always returns Allowed |
| `FakeBillingEngine` | In-memory fake for testing |
| `FakePremiumStateManager` | Configurable fake for testing |

## Platform Support

| Platform | Status |
|----------|--------|
| Android | Supported |
| iOS | Supported |
| Desktop (JVM) | Supported |

## Migration Guide: v1 → v2

Version 2.0.0 is a breaking change focused on end-to-end propagation of StoreKit 2 receipt
and transaction identifiers, so that KMP consumers can forward them to server-side receipt
validation without losing information at the bridge.

### 1. `PurchaseResult.Success` now requires `receipt`

**v1.x (removed)**:

```kotlin
PurchaseResult.Success(
    productId = product.id,
    purchaseToken = token
)
```

**v2.0+ (required)**:

```kotlin
PurchaseResult.Success(
    productId = product.id,
    purchaseToken = token,
    receipt = receipt  // Android: Purchase.originalJson; iOS: jwsRepresentation; "" otherwise
)
```

### 2. `IOSBillingBridge.requestPurchase.onSuccess` signature change

**v1.x (removed)**:

```kotlin
IOSBillingBridge.requestPurchase(
    productId = productId,
    onSuccess = { /* no payload */ },
    onError = { msg -> /* ... */ },
    onCancelled = { /* ... */ }
)
```

**v2.0+ (required)**:

```kotlin
IOSBillingBridge.requestPurchase(
    productId = productId,
    onSuccess = { receipt, transactionId ->
        // Forward these to your server verification layer.
    },
    onError = { msg -> /* ... */ },
    onCancelled = { /* ... */ }
)
```

### 3. `IOSBillingBridge.completePurchase` is now 5 parameters

Called from Swift to deliver the StoreKit outcome back to Kotlin.

**v1.x (removed)**:

```swift
IOSBillingBridge.shared.completePurchase(
    success: true,
    cancelled: false,
    error: nil
)
```

**v2.0+ (required)**:

```swift
// From your StoreKit 2 Transaction handler:
let receipt = transaction.jwsRepresentation
let transactionId = String(transaction.id)

IOSBillingBridge.shared.completePurchase(
    success: true,
    cancelled: false,
    error: nil,
    receipt: receipt,
    transactionId: transactionId
)

// Cancel / error branches: pass empty strings.
IOSBillingBridge.shared.completePurchase(
    success: false,
    cancelled: true,
    error: nil,
    receipt: "",
    transactionId: ""
)
```

### 4. Android consumers

`GooglePlayBillingEngine` currently passes `receipt = ""` because Google Play receipts are
delivered through `Purchase.originalJson` at the listener level rather than at purchase start.
Consumers that need Android server verification should populate `receipt` inside their own
`PurchaseResult.Success` construction or extend `GooglePlayBillingEngine` accordingly.

### 5. Fake / NoOp engines

`FakeBillingEngine.purchase` returns `receipt = "fake_receipt_<productId>"` so test assertions
can inspect the wiring without adopting real receipts. Update any test-side constructions of
`PurchaseResult.Success` to pass a receipt argument (empty string is fine).

### Backward Compatibility

None. All v1.x call sites of `PurchaseResult.Success` and the iOS purchase callbacks must be
updated before upgrading.

## License

Apache License 2.0
