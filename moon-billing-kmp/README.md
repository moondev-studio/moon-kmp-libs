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

## License

Apache License 2.0
