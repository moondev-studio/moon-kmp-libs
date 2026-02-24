package com.moondeveloper.analytics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CompositeAnalyticsTest {

    @Test
    fun logEvent_delegates_to_tracker() {
        val events = mutableListOf<AnalyticsEvent>()
        val tracker = RecordingTracker(events)
        val composite = createComposite(tracker = tracker)

        composite.logEvent(AnalyticsEvent("test"))
        assertEquals(1, events.size)
        assertEquals("test", events[0].name)
    }

    @Test
    fun trackScreenView_delegates_to_screenTracker() {
        val screens = mutableListOf<String>()
        val screenTracker = RecordingScreenTracker(screens)
        val composite = createComposite(screenTracker = screenTracker)

        composite.trackScreenView("HomeScreen", "HomeScreen")
        assertEquals(1, screens.size)
        assertEquals("HomeScreen", screens[0])
    }

    @Test
    fun trackButtonClick_delegates_to_actionTracker() {
        val actions = mutableListOf<String>()
        val actionTracker = RecordingUserActionTracker(actions)
        val composite = createComposite(actionTracker = actionTracker)

        composite.trackButtonClick("save_btn", "SettingsScreen")
        assertEquals(1, actions.size)
        assertTrue(actions[0].contains("save_btn"))
    }

    @Test
    fun trackPaywallShown_delegates_to_conversionTracker() {
        val triggers = mutableListOf<String>()
        val conversionTracker = RecordingConversionTracker(triggers)
        val composite = createComposite(conversionTracker = conversionTracker)

        composite.trackPaywallShown("settlement_limit")
        assertEquals(1, triggers.size)
        assertEquals("settlement_limit", triggers[0])
    }

    @Test
    fun setUserId_delegates_to_both_tracker_and_crashReporter() {
        val tracker = RecordingTracker()
        val crashReporter = RecordingCrashReporter()
        val composite = createComposite(tracker = tracker, crashReporter = crashReporter)

        composite.setUserId("user123")
        assertEquals("user123", tracker.lastUserId)
        assertEquals("user123", crashReporter.lastUserId)
    }

    @Test
    fun recordException_delegates_to_crashReporter() {
        val exceptions = mutableListOf<Throwable>()
        val crashReporter = RecordingCrashReporter(exceptions)
        val composite = createComposite(crashReporter = crashReporter)

        val error = RuntimeException("sync failed")
        composite.recordException(error, mapOf("screen" to "home"))
        assertEquals(1, exceptions.size)
        assertEquals("sync failed", exceptions[0].message)
    }

    @Test
    fun noOp_implementations_do_not_crash() {
        NoOpScreenTracker.trackScreenView("test")
        NoOpUserActionTracker.trackAction("test")
        NoOpUserActionTracker.trackButtonClick("btn", "screen")
        NoOpUserActionTracker.trackDialogResponse("dialog", "ok")
        NoOpUserActionTracker.trackFormSubmit("form", true)
        NoOpPerformanceTracker.startTrace("test").stop()
        NoOpPerformanceTracker.recordMetric("metric", 100L)
        NoOpTraceHandle.putAttribute("key", "value")
        NoOpTraceHandle.putMetric("key", 100L)
        NoOpConversionTracker.trackPaywallShown("trigger")
        NoOpConversionTracker.trackPaywallDismissed("trigger")
        NoOpConversionTracker.trackPurchaseStarted("id", "tier")
        NoOpConversionTracker.trackPurchaseCompleted("id", "tier", 100L, "USD")
        NoOpConversionTracker.trackPurchaseCancelled("id")
    }

    private fun createComposite(
        tracker: AnalyticsTracker = NoOpTracker,
        crashReporter: CrashReporter = NoOpCrashReporter,
        screenTracker: ScreenTracker = NoOpScreenTracker,
        actionTracker: UserActionTracker = NoOpUserActionTracker,
        performanceTracker: PerformanceTracker = NoOpPerformanceTracker,
        conversionTracker: ConversionTracker = NoOpConversionTracker
    ) = CompositeAnalytics(
        tracker = tracker,
        crashReporter = crashReporter,
        screenTracker = screenTracker,
        actionTracker = actionTracker,
        performanceTracker = performanceTracker,
        conversionTracker = conversionTracker
    )
}

private class RecordingTracker(
    private val eventLog: MutableList<AnalyticsEvent> = mutableListOf()
) : AnalyticsTracker {
    var lastUserId: String? = null
    override fun logEvent(event: AnalyticsEvent) { eventLog.add(event) }
    override fun setUserProperty(key: String, value: String) {}
    override fun setUserId(id: String?) { lastUserId = id }
}

private class RecordingCrashReporter(
    private val exceptionLog: MutableList<Throwable> = mutableListOf()
) : CrashReporter {
    var lastUserId: String? = null
    override fun recordException(throwable: Throwable, context: Map<String, String>) {
        exceptionLog.add(throwable)
    }
    override fun log(message: String) {}
    override fun setUserId(id: String?) { lastUserId = id }
}

private class RecordingScreenTracker(
    private val screenLog: MutableList<String> = mutableListOf()
) : ScreenTracker {
    override fun trackScreenView(screenName: String, screenClass: String?) {
        screenLog.add(screenName)
    }
}

private class RecordingUserActionTracker(
    private val actionLog: MutableList<String> = mutableListOf()
) : UserActionTracker {
    override fun trackAction(action: String, target: String?, params: Map<String, Any>) {
        actionLog.add("$action:$target")
    }
    override fun trackButtonClick(buttonName: String, screenName: String) {
        actionLog.add("button_click:$buttonName:$screenName")
    }
    override fun trackDialogResponse(dialogName: String, response: String) {
        actionLog.add("dialog:$dialogName:$response")
    }
    override fun trackFormSubmit(formName: String, success: Boolean) {
        actionLog.add("form:$formName:$success")
    }
}

private class RecordingConversionTracker(
    private val triggerLog: MutableList<String> = mutableListOf()
) : ConversionTracker {
    override fun trackPaywallShown(trigger: String) { triggerLog.add(trigger) }
    override fun trackPaywallDismissed(trigger: String) { triggerLog.add("dismiss:$trigger") }
    override fun trackPurchaseStarted(productId: String, tier: String) { triggerLog.add("start:$productId") }
    override fun trackPurchaseCompleted(productId: String, tier: String, price: Long, currency: String) {
        triggerLog.add("complete:$productId")
    }
    override fun trackPurchaseCancelled(productId: String) { triggerLog.add("cancel:$productId") }
}
