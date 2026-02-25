package com.moondeveloper.billing

/** Reactive state of the billing engine's purchase flow. */
sealed class PurchaseState {
    data object Idle : PurchaseState()
    data object Loading : PurchaseState()
    data class Purchased(val productId: String) : PurchaseState()
    data class Error(val message: String) : PurchaseState()
}
