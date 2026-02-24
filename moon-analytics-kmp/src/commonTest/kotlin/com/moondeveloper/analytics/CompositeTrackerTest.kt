package com.moondeveloper.analytics

import kotlin.test.Test
import kotlin.test.assertEquals

class CompositeTrackerTest {

    @Test
    fun logEvent_delegates_to_all_trackers() {
        val events1 = mutableListOf<AnalyticsEvent>()
        val events2 = mutableListOf<AnalyticsEvent>()
        val tracker1 = FakeTracker(events1)
        val tracker2 = FakeTracker(events2)
        val composite = CompositeTracker(listOf(tracker1, tracker2))
        val event = AnalyticsEvent("test_event", mapOf("key" to "value"))
        composite.logEvent(event)
        assertEquals(1, events1.size)
        assertEquals(1, events2.size)
        assertEquals("test_event", events1[0].name)
        assertEquals("value", events1[0].params["key"])
    }

    @Test
    fun setUserId_delegates_to_all_trackers() {
        val tracker1 = FakeTracker()
        val tracker2 = FakeTracker()
        val composite = CompositeTracker(listOf(tracker1, tracker2))
        composite.setUserId("user123")
        assertEquals("user123", tracker1.lastUserId)
        assertEquals("user123", tracker2.lastUserId)
    }

    @Test
    fun setUserProperty_delegates_to_all_trackers() {
        val tracker1 = FakeTracker()
        val tracker2 = FakeTracker()
        val composite = CompositeTracker(listOf(tracker1, tracker2))
        composite.setUserProperty("plan", "premium")
        assertEquals("premium", tracker1.properties["plan"])
        assertEquals("premium", tracker2.properties["plan"])
    }

    @Test
    fun empty_tracker_list_does_not_crash() {
        val composite = CompositeTracker(emptyList())
        composite.logEvent(AnalyticsEvent("test"))
        composite.setUserId("test")
        composite.setUserProperty("key", "value")
    }

    @Test
    fun noOpTracker_does_not_crash() {
        NoOpTracker.logEvent(AnalyticsEvent("test"))
        NoOpTracker.setUserId("test")
        NoOpTracker.setUserProperty("key", "value")
    }

    @Test
    fun noOpCrashReporter_does_not_crash() {
        NoOpCrashReporter.recordException(RuntimeException("test"))
        NoOpCrashReporter.log("test")
        NoOpCrashReporter.setUserId("test")
    }

    @Test
    fun compositeCrashReporter_delegates_to_all() {
        val exceptions1 = mutableListOf<Throwable>()
        val exceptions2 = mutableListOf<Throwable>()
        val reporter1 = FakeCrashReporter(exceptions1)
        val reporter2 = FakeCrashReporter(exceptions2)
        val composite = CompositeCrashReporter(listOf(reporter1, reporter2))
        val error = RuntimeException("test error")
        composite.recordException(error, mapOf("screen" to "home"))
        assertEquals(1, exceptions1.size)
        assertEquals(1, exceptions2.size)
        assertEquals("test error", exceptions1[0].message)
    }
}

private class FakeTracker(
    private val eventLog: MutableList<AnalyticsEvent> = mutableListOf()
) : AnalyticsTracker {
    var lastUserId: String? = null
    val properties = mutableMapOf<String, String>()
    override fun logEvent(event: AnalyticsEvent) { eventLog.add(event) }
    override fun setUserProperty(key: String, value: String) { properties[key] = value }
    override fun setUserId(id: String?) { lastUserId = id }
}

private class FakeCrashReporter(
    private val exceptionLog: MutableList<Throwable> = mutableListOf()
) : CrashReporter {
    override fun recordException(throwable: Throwable, context: Map<String, String>) { exceptionLog.add(throwable) }
    override fun log(message: String) {}
    override fun setUserId(id: String?) {}
}
