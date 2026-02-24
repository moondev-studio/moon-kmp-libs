package com.moondeveloper.analytics

import kotlin.test.Test
import kotlin.test.assertTrue

class AnalyticsSamplingTest {

    @Test
    fun screen_view_always_tracked() {
        repeat(100) {
            assertTrue(AnalyticsSampling.shouldTrack(EventCategory.SCREEN_VIEW))
        }
    }

    @Test
    fun user_action_always_tracked() {
        repeat(100) {
            assertTrue(AnalyticsSampling.shouldTrack(EventCategory.USER_ACTION))
        }
    }

    @Test
    fun error_always_tracked() {
        repeat(100) {
            assertTrue(AnalyticsSampling.shouldTrack(EventCategory.ERROR))
        }
    }

    @Test
    fun conversion_always_tracked() {
        repeat(100) {
            assertTrue(AnalyticsSampling.shouldTrack(EventCategory.CONVERSION))
        }
    }

    @Test
    fun performance_with_zero_rate_never_tracked() {
        repeat(100) {
            assertTrue(!AnalyticsSampling.shouldTrack(EventCategory.PERFORMANCE, 0.0f))
        }
    }

    @Test
    fun performance_with_full_rate_always_tracked() {
        repeat(100) {
            assertTrue(AnalyticsSampling.shouldTrack(EventCategory.PERFORMANCE, 1.0f))
        }
    }

    @Test
    fun performance_with_partial_rate_sometimes_tracked() {
        var trackedCount = 0
        val iterations = 1000
        repeat(iterations) {
            if (AnalyticsSampling.shouldTrack(EventCategory.PERFORMANCE, 0.5f)) {
                trackedCount++
            }
        }
        // With 50% sample rate, expect roughly 400-600 out of 1000
        assertTrue(trackedCount in 300..700, "Expected ~500 tracked but got $trackedCount")
    }

    @Test
    fun analyticsEvent_with_category_default() {
        val event = AnalyticsEvent("test")
        assertTrue(event.category == EventCategory.USER_ACTION)
        assertTrue(event.params.isEmpty())
    }

    @Test
    fun analyticsEvent_with_explicit_category() {
        val event = AnalyticsEvent("screen_view", EventCategory.SCREEN_VIEW, mapOf("screen" to "home"))
        assertTrue(event.category == EventCategory.SCREEN_VIEW)
        assertTrue(event.params["screen"] == "home")
    }

    @Test
    fun analyticsEvent_backward_compatible() {
        // Old usage: AnalyticsEvent("name", mapOf(...)) should still work via named params
        val event = AnalyticsEvent("test", params = mapOf("key" to "value"))
        assertTrue(event.name == "test")
        assertTrue(event.params["key"] == "value")
    }
}
