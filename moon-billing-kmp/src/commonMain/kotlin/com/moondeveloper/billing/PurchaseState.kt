package com.moondeveloper.billing

sealed class PurchaseState {
    data object Idle : PurchaseState()
    data object Loading : PurchaseState()
    data class Purchased(val productId: String) : PurchaseState()
    data class Error(val message: String) : PurchaseState()
}
