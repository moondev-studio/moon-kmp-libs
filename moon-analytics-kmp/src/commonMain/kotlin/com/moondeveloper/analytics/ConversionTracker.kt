package com.moondeveloper.analytics

/**
 * Conversion funnel tracking for paywall and purchase events.
 *
 * @see NoOpConversionTracker for testing
 */
interface ConversionTracker {
    /** Track when a paywall is displayed to the user. */
    fun trackPaywallShown(trigger: String)

    /** Track when a paywall is dismissed without purchase. */
    fun trackPaywallDismissed(trigger: String)

    /** Track when a user initiates a purchase flow. */
    fun trackPurchaseStarted(productId: String, tier: String)

    /** Track a successful purchase completion. */
    fun trackPurchaseCompleted(productId: String, tier: String, price: Long, currency: String)

    /** Track when a user cancels a purchase. */
    fun trackPurchaseCancelled(productId: String)
}
