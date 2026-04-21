package com.moondeveloper.billing

/** Result of a purchase or restore operation. */
sealed class PurchaseResult {
    /**
     * A successful purchase or restore.
     *
     * @param productId Store product identifier.
     * @param purchaseToken Platform-specific token (Android: BillingClient purchase token;
     *                      iOS: StoreKit 2 `Transaction.id` as String).
     * @param receipt Platform-specific server-verifiable receipt payload
     *                (Android: `Purchase.originalJson`; iOS: StoreKit 2 `jwsRepresentation`).
     *                Empty string when not applicable (e.g. restore path, fake engines).
     *
     * @since 2.0.0 — `receipt` parameter is now required. Previously the signature was
     *                `Success(productId, purchaseToken)` and server verification paths could
     *                not obtain a receipt through this type. See `moon-billing-kmp/README.md`
     *                Migration Guide for v1 → v2 details.
     */
    data class Success(
        val productId: String,
        val purchaseToken: String,
        val receipt: String
    ) : PurchaseResult()
    data class Cancelled(val productId: String) : PurchaseResult()
    data class Error(val exception: Throwable) : PurchaseResult()
    data class AlreadyOwned(val productId: String) : PurchaseResult()
}
