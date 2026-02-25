package com.moondeveloper.analytics

/**
 * Performance tracing interface for measuring operation durations and custom metrics.
 *
 * Performance events are subject to sampling in [CompositeAnalytics].
 *
 * @see NoOpPerformanceTracker for testing
 */
interface PerformanceTracker {
    /**
     * Start a named performance trace. Call [TraceHandle.stop] when the operation completes.
     *
     * @param traceName Unique trace name (e.g., "api_call", "db_query")
     * @return A handle to attach attributes/metrics and stop the trace
     */
    fun startTrace(traceName: String): TraceHandle

    /** Record a standalone numeric metric. */
    fun recordMetric(name: String, value: Long)
}

/**
 * Handle for an in-progress performance trace.
 * Attach attributes and metrics, then call [stop] to finalize.
 */
interface TraceHandle {
    /** Attach a string attribute to this trace. */
    fun putAttribute(key: String, value: String)

    /** Attach a numeric metric to this trace. */
    fun putMetric(key: String, value: Long)

    /** Stop the trace and submit the measurement. */
    fun stop()
}
