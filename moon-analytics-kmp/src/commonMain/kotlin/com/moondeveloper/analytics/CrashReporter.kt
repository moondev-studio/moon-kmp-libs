package com.moondeveloper.analytics

/**
 * Crash and error reporting interface.
 *
 * Platform implementations should delegate to Crashlytics, Sentry, or similar.
 *
 * @see CompositeCrashReporter for fan-out to multiple reporters
 * @see NoOpCrashReporter for testing
 */
interface CrashReporter {
    /**
     * Record a non-fatal exception with optional context metadata.
     *
     * @param throwable The exception to record
     * @param context Key-value pairs providing additional context
     */
    fun recordException(throwable: Throwable, context: Map<String, String> = emptyMap())

    /** Log a breadcrumb message for crash context. */
    fun log(message: String)

    /** Set the current user ID for crash attribution. Pass `null` to clear. */
    fun setUserId(id: String?)
}
