package com.moondeveloper.analytics

object NoOpTracker : AnalyticsTracker {
    override fun logEvent(event: AnalyticsEvent) {}
    override fun setUserProperty(key: String, value: String) {}
    override fun setUserId(id: String?) {}
}

object NoOpCrashReporter : CrashReporter {
    override fun recordException(throwable: Throwable, context: Map<String, String>) {}
    override fun log(message: String) {}
    override fun setUserId(id: String?) {}
}
