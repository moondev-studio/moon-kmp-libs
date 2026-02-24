package com.moondeveloper.billing

import kotlinx.coroutines.flow.Flow

/**
 * Platform-agnostic billing engine.
 * Implementations: Google Play Billing (Android), StoreKit 2 (iOS).
 */
interface BillingEngine {
    val availableProducts: Flow<List<Product>>
    val purchaseState: Flow<PurchaseState>

    suspend fun initialize()
    suspend fun queryProducts(productIds: List<String>): List<Product>
    suspend fun purchase(product: Product): PurchaseResult
    suspend fun restorePurchases(): PurchaseResult
    suspend fun consumePurchase(purchaseToken: String): Boolean
}
