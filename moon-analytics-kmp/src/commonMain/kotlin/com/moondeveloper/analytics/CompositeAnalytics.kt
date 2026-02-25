package com.moondeveloper.analytics

/**
 * Unified analytics facade that delegates to all tracker interfaces.
 *
 * Combines [AnalyticsTracker], [CrashReporter], [ScreenTracker], [UserActionTracker],
 * [PerformanceTracker], and [ConversionTracker] into a single entry point.
 *
 * Performance events are sampled at [performanceSampleRate] (default 10%).
 *
 * @param performanceSampleRate Probability (0.0-1.0) for performance event sampling
 */
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
