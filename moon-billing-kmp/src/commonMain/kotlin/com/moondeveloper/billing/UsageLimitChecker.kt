package com.moondeveloper.billing

/**
 * Feature usage limit enforcement.
 *
 * Checks whether the user can use a feature based on their tier and usage count.
 *
 * @see NoOpUsageLimitChecker for unsupported platforms (always allows)
 */
interface UsageLimitChecker {
    /** Check if the user can use the specified feature. */
    suspend fun canUse(feature: String): LimitCheckResult

    /** Record one usage of the specified feature. */
    suspend fun recordUsage(feature: String)

    /** Get remaining usage count for the specified feature. */
    suspend fun getRemainingCount(feature: String): Int
}

/** Result of a feature usage limit check. */
sealed class LimitCheckResult {
    data object Allowed : LimitCheckResult()
    data class LimitReached(val limit: Int, val used: Int) : LimitCheckResult()
    data object PremiumRequired : LimitCheckResult()
}
