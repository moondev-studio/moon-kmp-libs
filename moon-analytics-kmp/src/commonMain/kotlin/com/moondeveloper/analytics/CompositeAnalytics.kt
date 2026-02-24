package com.moondeveloper.analytics

class CompositeAnalytics(
    private val tracker: AnalyticsTracker,
    private val crashReporter: CrashReporter,
    private val screenTracker: ScreenTracker,
    private val actionTracker: UserActionTracker,
    private val performanceTracker: PerformanceTracker,
    private val conversionTracker: ConversionTracker,
    private val performanceSampleRate: Float = 0.1f
) : AnalyticsTracker by tracker,
    CrashReporter by crashReporter,
    ScreenTracker by screenTracker,
    UserActionTracker by actionTracker,
    PerformanceTracker by performanceTracker,
    ConversionTracker by conversionTracker {

    override fun setUserId(id: String?) {
        tracker.setUserId(id)
        crashReporter.setUserId(id)
    }

    override fun startTrace(traceName: String): TraceHandle {
        return if (AnalyticsSampling.shouldTrack(EventCategory.PERFORMANCE, performanceSampleRate)) {
            performanceTracker.startTrace(traceName)
        } else {
            NoOpTraceHandle
        }
    }

    override fun recordMetric(name: String, value: Long) {
        if (AnalyticsSampling.shouldTrack(EventCategory.PERFORMANCE, performanceSampleRate)) {
            performanceTracker.recordMetric(name, value)
        }
    }
}
