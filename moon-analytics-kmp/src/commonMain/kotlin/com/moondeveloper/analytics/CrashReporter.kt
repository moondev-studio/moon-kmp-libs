package com.moondeveloper.analytics

interface CrashReporter {
    fun recordException(throwable: Throwable, context: Map<String, String> = emptyMap())
    fun log(message: String)
    fun setUserId(id: String?)
}
