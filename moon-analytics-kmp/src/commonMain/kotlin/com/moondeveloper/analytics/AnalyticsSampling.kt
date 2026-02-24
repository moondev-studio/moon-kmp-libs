package com.moondeveloper.analytics

import kotlin.random.Random

object AnalyticsSampling {
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
