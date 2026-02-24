package com.moondeveloper.analytics

interface AnalyticsTracker {
    fun logEvent(event: AnalyticsEvent)
    fun setUserProperty(key: String, value: String)
    fun setUserId(id: String?)
}

data class AnalyticsEvent(
    val name: String,
    val category: EventCategory = EventCategory.USER_ACTION,
    val params: Map<String, Any> = emptyMap()
)

enum class EventCategory {
    SCREEN_VIEW,
    USER_ACTION,
    FEATURE_USAGE,
    ERROR,
    PERFORMANCE,
    CONVERSION,
    RETENTION
}
