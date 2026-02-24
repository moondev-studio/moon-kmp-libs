package com.moondeveloper.analytics

interface AnalyticsTracker {
    fun logEvent(event: AnalyticsEvent)
    fun setUserProperty(key: String, value: String)
    fun setUserId(id: String?)
}

data class AnalyticsEvent(
    val name: String,
    val params: Map<String, Any> = emptyMap()
)
