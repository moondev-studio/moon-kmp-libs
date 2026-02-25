package com.moondeveloper.analytics

/**
 * Core analytics event tracking interface.
 *
 * Platform implementations should delegate to the underlying analytics SDK
 * (e.g., Firebase Analytics on Android/iOS).
 *
 * @see CompositeTracker for fan-out to multiple tracker instances
 * @see NoOpTracker for testing and unsupported platforms
 */
interface AnalyticsTracker {
    /** Log a structured analytics event. */
    fun logEvent(event: AnalyticsEvent)

    /** Set a user-scoped property for segmentation. */
    fun setUserProperty(key: String, value: String)

    /** Set the current user ID for attribution. Pass `null` to clear. */
    fun setUserId(id: String?)
}

/**
 * Structured analytics event.
 *
 * @property name Event name (e.g., "screen_view", "button_click")
 * @property category Event category for filtering and sampling
 * @property params Additional key-value context
 */
data class AnalyticsEvent(
    val name: String,
    val category: EventCategory = EventCategory.USER_ACTION,
    val params: Map<String, Any> = emptyMap()
)

/** Classification of analytics events, used for filtering and sampling. */
enum class EventCategory {
    SCREEN_VIEW,
    USER_ACTION,
    FEATURE_USAGE,
    ERROR,
    PERFORMANCE,
    CONVERSION,
    RETENTION
}
