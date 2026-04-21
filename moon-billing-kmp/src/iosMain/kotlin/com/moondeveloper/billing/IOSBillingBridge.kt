package com.moondeveloper.billing

/**
 * iOS StoreKit 2 bridge for IAP operations.
 *
 * Swift (AppDelegate) registers start closures, and
 * Kotlin (StoreKitBillingEngine) calls request methods which trigger Swift-side logic.
 * Swift then calls complete methods to deliver results back to Kotlin.
 *
 * ## v2.0.0 breaking change
 * The purchase callback chain now propagates the StoreKit 2 `jwsRepresentation` (receipt)
 * and `Transaction.id` (transactionId) all the way from Swift to the KMP consumer, enabling
 * server-side receipt verification. The following shapes changed:
 *
 * - `requestPurchase.onSuccess`: `() -> Unit` → `(receipt: String, transactionId: String) -> Unit`
 * - `completePurchase(success, cancelled, error)` → `completePurchase(success, cancelled, error, receipt, transactionId)`
 *
 * Swift callers must be updated to forward `transaction.jwsRepresentation` and
 * `String(transaction.id)` into the 5-parameter `completePurchase`. See the Migration Guide in
 * `moon-billing-kmp/README.md`.
 */
object IOSBillingBridge {

    var startLoadProduct: ((productId: String) -> Unit)? = null
    var startPurchase: ((productId: String) -> Unit)? = null
    var startRestore: (() -> Unit)? = null

    private var loadProductSuccessCallback: ((price: String) -> Unit)? = null
    private var loadProductErrorCallback: ((String) -> Unit)? = null
    private var purchaseSuccessCallback: ((receipt: String, transactionId: String) -> Unit)? = null
    private var purchaseErrorCallback: ((String) -> Unit)? = null
    private var purchaseCancelledCallback: (() -> Unit)? = null
    private var restoreSuccessCallback: ((hasLifetime: Boolean) -> Unit)? = null
    private var restoreErrorCallback: ((String) -> Unit)? = null

    fun requestLoadProduct(
        productId: String,
        onSuccess: (price: String) -> Unit,
        onError: (String) -> Unit
    ) {
        loadProductSuccessCallback = onSuccess
        loadProductErrorCallback = onError
        val loader = startLoadProduct
        if (loader != null) {
            loader(productId)
        } else {
            onError("Billing bridge not configured")
            loadProductSuccessCallback = null
            loadProductErrorCallback = null
        }
    }

    fun completeLoadProduct(price: String?, error: String?) {
        if (error != null || price == null) {
            loadProductErrorCallback?.invoke(error ?: "Unknown error")
        } else {
            loadProductSuccessCallback?.invoke(price)
        }
        loadProductSuccessCallback = null
        loadProductErrorCallback = null
    }

    /**
     * Start a purchase flow. `onSuccess` receives the StoreKit 2 receipt payload and the
     * transaction identifier so that the KMP consumer can forward them to its server.
     *
     * @since 2.0.0 — `onSuccess` signature changed from `() -> Unit` to
     *                `(receipt: String, transactionId: String) -> Unit`.
     */
    fun requestPurchase(
        productId: String,
        onSuccess: (receipt: String, transactionId: String) -> Unit,
        onError: (String) -> Unit,
        onCancelled: () -> Unit
    ) {
        purchaseSuccessCallback = onSuccess
        purchaseErrorCallback = onError
        purchaseCancelledCallback = onCancelled
        val purchaser = startPurchase
        if (purchaser != null) {
            purchaser(productId)
        } else {
            onError("Billing bridge not configured")
            purchaseSuccessCallback = null
            purchaseErrorCallback = null
            purchaseCancelledCallback = null
        }
    }

    /**
     * Swift side calls this to deliver the outcome of a purchase flow.
     *
     * @param success `true` when StoreKit reported a verified purchase.
     * @param cancelled `true` when the user cancelled the StoreKit sheet.
     * @param error Non-null when a purchase failed for any reason other than cancellation.
     * @param receipt StoreKit 2 `Transaction.jwsRepresentation`. Must be non-blank when
     *                `success` is `true`; use empty string for cancel/error branches.
     * @param transactionId StoreKit 2 `String(Transaction.id)`. Must be non-blank when
     *                      `success` is `true`; use empty string for cancel/error branches.
     *
     * @since 2.0.0 — Breaking change: `receipt` and `transactionId` parameters are now required.
     *                Previous signature `(success, cancelled, error)` has been removed.
     */
    fun completePurchase(
        success: Boolean,
        cancelled: Boolean,
        error: String?,
        receipt: String,
        transactionId: String
    ) {
        when {
            cancelled -> purchaseCancelledCallback?.invoke()
            error != null || !success -> purchaseErrorCallback?.invoke(error ?: "Purchase failed")
            else -> purchaseSuccessCallback?.invoke(receipt, transactionId)
        }
        purchaseSuccessCallback = null
        purchaseErrorCallback = null
        purchaseCancelledCallback = null
    }

    fun requestRestore(
        onSuccess: (hasLifetime: Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        restoreSuccessCallback = onSuccess
        restoreErrorCallback = onError
        val restorer = startRestore
        if (restorer != null) {
            restorer()
        } else {
            onError("Billing bridge not configured")
            restoreSuccessCallback = null
            restoreErrorCallback = null
        }
    }

    fun completeRestore(hasLifetime: Boolean, error: String?) {
        if (error != null) {
            restoreErrorCallback?.invoke(error)
        } else {
            restoreSuccessCallback?.invoke(hasLifetime)
        }
        restoreSuccessCallback = null
        restoreErrorCallback = null
    }
}
