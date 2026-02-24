package com.moondeveloper.billing

interface UsageLimitChecker {
    suspend fun canUse(feature: String): LimitCheckResult
    suspend fun recordUsage(feature: String)
    suspend fun getRemainingCount(feature: String): Int
}

sealed class LimitCheckResult {
    data object Allowed : LimitCheckResult()
    data class LimitReached(val limit: Int, val used: Int) : LimitCheckResult()
    data object PremiumRequired : LimitCheckResult()
}
