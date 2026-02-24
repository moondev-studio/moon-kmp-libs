package com.moondeveloper.billing

sealed class PurchaseResult {
    data class Success(val productId: String, val purchaseToken: String) : PurchaseResult()
    data class Cancelled(val productId: String) : PurchaseResult()
    data class Error(val exception: Throwable) : PurchaseResult()
    data class AlreadyOwned(val productId: String) : PurchaseResult()
}
