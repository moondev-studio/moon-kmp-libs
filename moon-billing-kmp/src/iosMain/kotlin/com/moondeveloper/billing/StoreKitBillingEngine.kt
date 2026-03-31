package com.moondeveloper.billing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * StoreKit 2 [BillingEngine] implementation for iOS.
 *
 * Delegates to [IOSBillingBridge] which is configured from the Swift side.
 */
class StoreKitBillingEngine : BillingEngine {

    private val _availableProducts = MutableStateFlow<List<Product>>(emptyList())
    override val availableProducts: Flow<List<Product>> = _availableProducts.asStateFlow()

    private val _purchaseState = MutableStateFlow<PurchaseState>(PurchaseState.Idle)
    override val purchaseState: Flow<PurchaseState> = _purchaseState.asStateFlow()

    override suspend fun initialize() {
        // StoreKit initialization happens via bridge registration from Swift side
    }

    override suspend fun queryProducts(productIds: List<String>): List<Product> {
        return suspendCancellableCoroutine { cont ->
            val productId = productIds.firstOrNull()
            if (productId == null) {
                if (cont.isActive) cont.resume(emptyList())
                return@suspendCancellableCoroutine
            }

            IOSBillingBridge.requestLoadProduct(
                productId = productId,
                onSuccess = { price ->
                    val product = Product(
                        id = productId,
                        name = productId,
                        description = "",
                        price = price,
                        priceMicros = 0L,
                        currencyCode = "",
                        type = ProductType.ONE_TIME
                    )
                    _availableProducts.value = listOf(product)
                    if (cont.isActive) cont.resume(listOf(product))
                },
                onError = { errorMsg ->
                    println("[Splitly-IAP] queryProducts error: $errorMsg")
                    if (cont.isActive) cont.resume(emptyList())
                }
            )
        }
    }

    override suspend fun purchase(product: Product): PurchaseResult {
        _purchaseState.value = PurchaseState.Loading

        return suspendCancellableCoroutine { cont ->
            IOSBillingBridge.requestPurchase(
                productId = product.id,
                onSuccess = {
                    _purchaseState.value = PurchaseState.Purchased(product.id)
                    if (cont.isActive) cont.resume(
                        PurchaseResult.Success(
                            productId = product.id,
                            purchaseToken = "ios_purchase"
                        )
                    )
                },
                onError = { errorMsg ->
                    println("[Splitly-IAP] purchase error: $errorMsg productId=${product.id}")
                    _purchaseState.value = PurchaseState.Error(errorMsg)
                    if (cont.isActive) cont.resume(
                        PurchaseResult.Error(RuntimeException(errorMsg))
                    )
                },
                onCancelled = {
                    _purchaseState.value = PurchaseState.Idle
                    if (cont.isActive) cont.resume(
                        PurchaseResult.Cancelled(product.id)
                    )
                }
            )
        }
    }

    override suspend fun restorePurchases(): PurchaseResult {
        return suspendCancellableCoroutine { cont ->
            IOSBillingBridge.requestRestore(
                onSuccess = { hasLifetime ->
                    if (hasLifetime) {
                        _purchaseState.value = PurchaseState.Purchased("restored")
                        if (cont.isActive) cont.resume(
                            PurchaseResult.Success(
                                productId = "restored",
                                purchaseToken = "ios_restore"
                            )
                        )
                    } else {
                        if (cont.isActive) cont.resume(
                            PurchaseResult.Error(RuntimeException("No purchases found"))
                        )
                    }
                },
                onError = { errorMsg ->
                    println("[Splitly-IAP] restore error: $errorMsg")
                    if (cont.isActive) cont.resume(
                        PurchaseResult.Error(RuntimeException(errorMsg))
                    )
                }
            )
        }
    }

    override suspend fun consumePurchase(purchaseToken: String): Boolean {
        // iOS doesn't have a consumePurchase concept for non-consumable items
        return false
    }
}
