package com.moondeveloper.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.resume

/**
 * Google Play Billing [BillingEngine] implementation.
 *
 * Uses [BillingClient] for in-app purchase operations on Android.
 * Call [destroy] when the engine is no longer needed to release resources.
 */
class GooglePlayBillingEngine(
    private val context: Context
) : BillingEngine {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _availableProducts = MutableStateFlow<List<Product>>(emptyList())
    override val availableProducts: Flow<List<Product>> = _availableProducts.asStateFlow()

    private val _purchaseState = MutableStateFlow<PurchaseState>(PurchaseState.Idle)
    override val purchaseState: Flow<PurchaseState> = _purchaseState.asStateFlow()

    private var billingClient: BillingClient? = null
    private val productDetailsMap = mutableMapOf<String, ProductDetails>()
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 3

    // Active purchase continuation — set when purchase() is awaiting the async
    // onPurchasesUpdated callback. Resumed with the real purchaseToken + orderId.
    // WP[139]: receipt/transactionId must carry the real Play Billing values.
    private val pendingPurchase = AtomicReference<PendingPurchase?>(null)

    private data class PendingPurchase(
        val productId: String,
        val continuation: CancellableContinuation<PurchaseResult>
    )

    companion object {
        private var activityRef: WeakReference<Activity>? = null

        fun setActivity(activity: Activity) {
            activityRef = WeakReference(activity)
        }
    }

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchases.isNullOrEmpty()) {
                    completePendingPurchase(
                        PurchaseResult.Error(RuntimeException("OK response but no purchases"))
                    )
                    return@PurchasesUpdatedListener
                }
                scope.launch {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                _purchaseState.value = PurchaseState.Idle
                val pending = pendingPurchase.getAndSet(null)
                pending?.continuation?.takeIf { it.isActive }?.resume(
                    PurchaseResult.Cancelled(pending.productId)
                )
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                val pending = pendingPurchase.getAndSet(null)
                scope.launch { restorePurchases() }
                pending?.continuation?.takeIf { it.isActive }?.resume(
                    PurchaseResult.AlreadyOwned(pending.productId)
                )
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                _purchaseState.value = PurchaseState.Error(
                    "Billing service unavailable. Please try again."
                )
                startConnection()
                completePendingPurchase(
                    PurchaseResult.Error(
                        RuntimeException(
                            "Billing service unavailable (code ${billingResult.responseCode})"
                        )
                    )
                )
            }
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> {
                _purchaseState.value = PurchaseState.Error(
                    "Item unavailable: ${billingResult.debugMessage ?: "unknown"}"
                )
                completePendingPurchase(
                    PurchaseResult.Error(
                        RuntimeException("Item unavailable: ${billingResult.debugMessage}")
                    )
                )
            }
            else -> {
                _purchaseState.value = PurchaseState.Error(
                    "Code ${billingResult.responseCode}: ${billingResult.debugMessage ?: "Purchase failed"}"
                )
                completePendingPurchase(
                    PurchaseResult.Error(
                        RuntimeException(
                            "Billing error ${billingResult.responseCode}: ${billingResult.debugMessage}"
                        )
                    )
                )
            }
        }
    }

    private fun completePendingPurchase(result: PurchaseResult) {
        val pending = pendingPurchase.getAndSet(null) ?: return
        pending.continuation.takeIf { it.isActive }?.resume(result)
    }

    override suspend fun initialize() {
        if (billingClient != null) return

        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build()
            )
            .build()

        startConnection()
    }

    private fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    reconnectAttempts = 0
                    scope.launch { queryExistingPurchases() }
                }
            }

            override fun onBillingServiceDisconnected() {
                if (reconnectAttempts < maxReconnectAttempts) {
                    reconnectAttempts++
                    scope.launch {
                        delay(2000L * reconnectAttempts)
                        if (billingClient?.isReady == false) {
                            startConnection()
                        }
                    }
                }
            }
        })
    }

    private suspend fun queryExistingPurchases() {
        val client = billingClient ?: return
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        client.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                scope.launch {
                    for (purchase in purchases) {
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                            handlePurchase(purchase)
                        }
                    }
                }
            }
        }
    }

    override suspend fun queryProducts(productIds: List<String>): List<Product> {
        val client = billingClient ?: return emptyList()

        return suspendCancellableCoroutine { cont ->
            val productList = productIds.map { id ->
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(id)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            }

            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            client.queryProductDetailsAsync(params) { billingResult, detailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val products = detailsList.mapNotNull { details ->
                        val offerDetails = details.oneTimePurchaseOfferDetails
                            ?: return@mapNotNull null
                        productDetailsMap[details.productId] = details
                        Product(
                            id = details.productId,
                            name = details.name,
                            description = details.description,
                            price = offerDetails.formattedPrice,
                            priceMicros = offerDetails.priceAmountMicros,
                            currencyCode = offerDetails.priceCurrencyCode,
                            type = ProductType.ONE_TIME
                        )
                    }
                    _availableProducts.value = products
                    if (cont.isActive) cont.resume(products)
                } else {
                    if (cont.isActive) cont.resume(emptyList())
                }
            }
        }
    }

    override suspend fun purchase(product: Product): PurchaseResult = withContext(Dispatchers.Main) {
        val client = billingClient
            ?: return@withContext PurchaseResult.Error(IllegalStateException("BillingClient not initialized"))
        val details = productDetailsMap[product.id]
            ?: return@withContext PurchaseResult.Error(IllegalStateException("Product details not found"))
        val activity = activityRef?.get()
            ?: return@withContext PurchaseResult.Error(IllegalStateException("Activity not available"))

        if (activity.isFinishing) {
            return@withContext PurchaseResult.Error(IllegalStateException("Activity is finishing"))
        }

        suspendCancellableCoroutine<PurchaseResult> { cont ->
            val newPending = PendingPurchase(productId = product.id, continuation = cont)
            if (!pendingPurchase.compareAndSet(null, newPending)) {
                if (cont.isActive) cont.resume(
                    PurchaseResult.Error(IllegalStateException("A purchase is already in progress"))
                )
                return@suspendCancellableCoroutine
            }

            cont.invokeOnCancellation {
                pendingPurchase.compareAndSet(newPending, null)
            }

            _purchaseState.value = PurchaseState.Loading

            val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .build()

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productDetailsParams))
                .build()

            val billingResult = client.launchBillingFlow(activity, billingFlowParams)

            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                pendingPurchase.compareAndSet(newPending, null)
                _purchaseState.value = PurchaseState.Error(
                    billingResult.debugMessage ?: "launchBillingFlow failed"
                )
                if (cont.isActive) cont.resume(
                    PurchaseResult.Error(
                        RuntimeException(billingResult.debugMessage ?: "launchBillingFlow failed")
                    )
                )
            }
            // On OK: keep suspending; purchasesUpdatedListener → handlePurchase will
            // resume with the real purchaseToken + orderId (WP[139] fix).
        }
    }

    override suspend fun restorePurchases(): PurchaseResult {
        val client = billingClient
            ?: return PurchaseResult.Error(IllegalStateException("BillingClient not initialized"))

        return suspendCancellableCoroutine { cont ->
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()

            client.queryPurchasesAsync(params) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    scope.launch {
                        var restored = false
                        for (purchase in purchases) {
                            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                handlePurchase(purchase)
                                restored = true
                            }
                        }
                        if (restored) {
                            if (cont.isActive) cont.resume(
                                PurchaseResult.Success(
                                    productId = "restored",
                                    purchaseToken = "restored",
                                    receipt = ""
                                )
                            )
                        } else {
                            if (cont.isActive) cont.resume(
                                PurchaseResult.Error(RuntimeException("No purchases found"))
                            )
                        }
                    }
                } else {
                    if (cont.isActive) cont.resume(
                        PurchaseResult.Error(
                            RuntimeException(billingResult.debugMessage ?: "Restore failed")
                        )
                    )
                }
            }
        }
    }

    override suspend fun consumePurchase(purchaseToken: String): Boolean {
        val client = billingClient ?: return false
        return suspendCancellableCoroutine { cont ->
            val params = com.android.billingclient.api.ConsumeParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build()
            client.consumeAsync(params) { billingResult, _ ->
                if (cont.isActive) {
                    cont.resume(billingResult.responseCode == BillingClient.BillingResponseCode.OK)
                }
            }
        }
    }

    /**
     * Release billing client connection and cancel coroutine scope.
     * Must be called when the engine is no longer needed (e.g., Activity.onDestroy).
     */
    fun destroy() {
        billingClient?.endConnection()
        billingClient = null
        productDetailsMap.clear()
        scope.cancel()
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) {
            // PENDING / UNSPECIFIED — surface as error to any awaiting purchase()
            completePendingPurchase(
                PurchaseResult.Error(
                    RuntimeException("Purchase not complete: state=${purchase.purchaseState}")
                )
            )
            return
        }
        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            val result = billingClient?.acknowledgePurchase(params)
            if (result?.responseCode != BillingClient.BillingResponseCode.OK) {
                _purchaseState.value = PurchaseState.Error(
                    "Acknowledgment failed: ${result?.debugMessage}"
                )
                completePendingPurchase(
                    PurchaseResult.Error(
                        RuntimeException("Acknowledgment failed: ${result?.debugMessage}")
                    )
                )
                return
            }
        }
        for (productId in purchase.products) {
            _purchaseState.value = PurchaseState.Purchased(productId)
        }
        // WP[139] fix: propagate the real Play Billing identifiers up to
        // BillingEngineAdapter so server verification receives purchaseToken
        // instead of the previous "" placeholder. Mapping matches iOS:
        //   PurchaseReceipt.receipt      = purchase.purchaseToken  (server verify)
        //   PurchaseReceipt.transactionId = purchase.orderId       (audit trail)
        val productId = purchase.products.firstOrNull()
            ?: pendingPurchase.get()?.productId
            ?: ""
        completePendingPurchase(
            PurchaseResult.Success(
                productId = productId,
                purchaseToken = purchase.orderId ?: "",
                receipt = purchase.purchaseToken
            )
        )
    }
}
