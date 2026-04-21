package com.moondeveloper.billing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * In-memory fake [BillingEngine] for unit testing.
 *
 * Configure products with [setProducts], inject failures with [shouldFail],
 * and inspect state via [purchasedProduct], [restoreCalled], etc.
 */
class FakeBillingEngine : BillingEngine {
    var shouldFail: Boolean = false
    var failException: Throwable = RuntimeException("Fake error")

    private val _availableProducts = MutableStateFlow<List<Product>>(emptyList())
    override val availableProducts: Flow<List<Product>> = _availableProducts

    private val _purchaseState = MutableStateFlow<PurchaseState>(PurchaseState.Idle)
    override val purchaseState: Flow<PurchaseState> = _purchaseState

    var initialized = false
        private set
    var queriedProductIds: List<String> = emptyList()
        private set
    var purchasedProduct: Product? = null
        private set
    var restoreCalled = false
        private set
    var consumedToken: String? = null
        private set

    fun setProducts(products: List<Product>) {
        _availableProducts.value = products
    }

    fun setPurchaseState(state: PurchaseState) {
        _purchaseState.value = state
    }

    override suspend fun initialize() {
        initialized = true
    }

    override suspend fun queryProducts(productIds: List<String>): List<Product> {
        queriedProductIds = productIds
        if (shouldFail) throw failException
        return _availableProducts.value.filter { it.id in productIds }
    }

    override suspend fun purchase(product: Product): PurchaseResult {
        purchasedProduct = product
        if (shouldFail) return PurchaseResult.Error(failException)
        _purchaseState.value = PurchaseState.Purchased(product.id)
        return PurchaseResult.Success(
            productId = product.id,
            purchaseToken = "fake_token_${product.id}",
            receipt = "fake_receipt_${product.id}"
        )
    }

    override suspend fun restorePurchases(): PurchaseResult {
        restoreCalled = true
        if (shouldFail) return PurchaseResult.Error(failException)
        return PurchaseResult.Success(
            productId = "restored",
            purchaseToken = "fake_restore_token",
            receipt = ""
        )
    }

    override suspend fun consumePurchase(purchaseToken: String): Boolean {
        consumedToken = purchaseToken
        return !shouldFail
    }
}
