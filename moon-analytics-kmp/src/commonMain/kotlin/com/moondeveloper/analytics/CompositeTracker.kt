package com.moondeveloper.analytics

class CompositeTracker(
    private val trackers: List<AnalyticsTracker>
) : AnalyticsTracker {
    override fun logEvent(event: AnalyticsEvent) {
        trackers.forEach { it.logEvent(event) }
    }
    override fun setUserProperty(key: String, value: String) {
        trackers.forEach { it.setUserProperty(key, value) }
    }
    override fun setUserId(id: String?) {
        trackers.forEach { it.setUserId(id) }
    }
}

class CompositeCrashReporter(
    private val reporters: List<CrashReporter>
) : CrashReporter {
    override fun recordException(throwable: Throwable, context: Map<String, String>) {
        reporters.forEach { it.recordException(throwable, context) }
    }
    override fun log(message: String) {
        reporters.forEach { it.log(message) }
    }
    override fun setUserId(id: String?) {
        reporters.forEach { it.setUserId(id) }
    }
}
