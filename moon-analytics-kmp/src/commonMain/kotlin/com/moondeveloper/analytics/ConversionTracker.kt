package com.moondeveloper.analytics

interface ConversionTracker {
    fun trackPaywallShown(trigger: String)
    fun trackPaywallDismissed(trigger: String)
    fun trackPurchaseStarted(productId: String, tier: String)
    fun trackPurchaseCompleted(productId: String, tier: String, price: Long, currency: String)
    fun trackPurchaseCancelled(productId: String)
}
