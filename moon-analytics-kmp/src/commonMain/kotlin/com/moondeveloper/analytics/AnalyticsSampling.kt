package com.moondeveloper.analytics

import kotlin.random.Random

/**
 * Sampling logic for analytics events.
 *
 * All categories are tracked at 100% except [EventCategory.PERFORMANCE],
 * which is probabilistically sampled at the given rate.
 */
object AnalyticsSampling {
    /**
     * Determine whether an event should be tracked based on its category and sample rate.
     *
     * @param category The event category
     * @param sampleRate Probability (0.0-1.0) for performance events; ignored for other categories
     * @return `true` if the event should be tracked
     */
    fun shouldTrack(category: EventCategory, sampleRate: Float = 1.0f): Boolean {
        return when (category) {
            EventCategory.SCREEN_VIEW -> true
            EventCategory.USER_ACTION -> true
            EventCategory.FEATURE_USAGE -> true
            EventCategory.ERROR -> true
            EventCategory.CONVERSION -> true
            EventCategory.RETENTION -> true
            EventCategory.PERFORMANCE -> Random.nextFloat() < sampleRate
        }
    }
}
