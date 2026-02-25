package com.moondeveloper.analytics

/** No-op [AnalyticsTracker] for testing and unsupported platforms. */
object NoOpTracker : AnalyticsTracker {
    override fun logEvent(event: AnalyticsEvent) {}
    override fun setUserProperty(key: String, value: String) {}
    override fun setUserId(id: String?) {}
}

/** No-op [CrashReporter] for testing and unsupported platforms. */
object NoOpCrashReporter : CrashReporter {
    override fun recordException(throwable: Throwable, context: Map<String, String>) {}
    override fun log(message: String) {}
    override fun setUserId(id: String?) {}
}

/** No-op [ScreenTracker] for testing and unsupported platforms. */
object NoOpScreenTracker : ScreenTracker {
    override fun trackScreenView(screenName: String, screenClass: String?) {}
}

/** No-op [UserActionTracker] for testing and unsupported platforms. */
object NoOpUserActionTracker : UserActionTracker {
    override fun trackAction(action: String, target: String?, params: Map<String, Any>) {}
    override fun trackButtonClick(buttonName: String, screenName: String) {}
    override fun trackDialogResponse(dialogName: String, response: String) {}
    override fun trackFormSubmit(formName: String, success: Boolean) {}
}

/** No-op [PerformanceTracker] for testing and unsupported platforms. */
object NoOpPerformanceTracker : PerformanceTracker {
    override fun startTrace(traceName: String): TraceHandle = NoOpTraceHandle
    override fun recordMetric(name: String, value: Long) {}
}

/** No-op [TraceHandle] that discards all attributes and metrics. */
object NoOpTraceHandle : TraceHandle {
    override fun putAttribute(key: String, value: String) {}
    override fun putMetric(key: String, value: Long) {}
    override fun stop() {}
}

/** No-op [ConversionTracker] for testing and unsupported platforms. */
object NoOpConversionTracker : ConversionTracker {
    override fun trackPaywallShown(trigger: String) {}
    override fun trackPaywallDismissed(trigger: String) {}
    override fun trackPurchaseStarted(productId: String, tier: String) {}
    override fun trackPurchaseCompleted(productId: String, tier: String, price: Long, currency: String) {}
    override fun trackPurchaseCancelled(productId: String) {}
}
