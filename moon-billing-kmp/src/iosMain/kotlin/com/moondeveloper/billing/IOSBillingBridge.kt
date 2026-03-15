package com.moondeveloper.billing

/**
 * iOS StoreKit 2 bridge for IAP operations.
 *
 * Swift (AppDelegate) registers start closures, and
 * Kotlin (StoreKitBillingEngine) calls request methods which trigger Swift-side logic.
 * Swift then calls complete methods to deliver results back to Kotlin.
 */
object IOSBillingBridge {

    var startLoadProduct: ((productId: String) -> Unit)? = null
    var startPurchase: ((productId: String) -> Unit)? = null
    var startRestore: (() -> Unit)? = null

    private var loadProductSuccessCallback: ((price: String) -> Unit)? = null
    private var loadProductErrorCallback: ((String) -> Unit)? = null
    private var purchaseSuccessCallback: (() -> Unit)? = null
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

    fun requestPurchase(
        productId: String,
        onSuccess: () -> Unit,
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

    fun completePurchase(success: Boolean, cancelled: Boolean, error: String?) {
        when {
            cancelled -> purchaseCancelledCallback?.invoke()
            error != null || !success -> purchaseErrorCallback?.invoke(error ?: "Purchase failed")
            else -> purchaseSuccessCallback?.invoke()
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
