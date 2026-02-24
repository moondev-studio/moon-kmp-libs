package com.moondeveloper.analytics

interface PerformanceTracker {
    fun startTrace(traceName: String): TraceHandle
    fun recordMetric(name: String, value: Long)
}

interface TraceHandle {
    fun putAttribute(key: String, value: String)
    fun putMetric(key: String, value: Long)
    fun stop()
}
