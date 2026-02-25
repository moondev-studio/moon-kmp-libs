package com.moondeveloper.billing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** No-op [BillingEngine] for unsupported platforms. Purchase operations return errors. */
object NoOpBillingEngine : BillingEngine {
    override val availableProducts: Flow<List<Product>> = MutableStateFlow(emptyList())
    override val purchaseState: Flow<PurchaseState> = MutableStateFlow(PurchaseState.Idle)
    override suspend fun initialize() {}
    override suspend fun queryProducts(productIds: List<String>): List<Product> = emptyList()
    override suspend fun purchase(product: Product): PurchaseResult = PurchaseResult.Error(UnsupportedOperationException("NoOp"))
    override suspend fun restorePurchases(): PurchaseResult = PurchaseResult.Error(UnsupportedOperationException("NoOp"))
    override suspend fun consumePurchase(purchaseToken: String): Boolean = false
}
