package com.moondeveloper.billing

object NoOpUsageLimitChecker : UsageLimitChecker {
    override suspend fun canUse(feature: String): LimitCheckResult = LimitCheckResult.Allowed
    override suspend fun recordUsage(feature: String) {}
    override suspend fun getRemainingCount(feature: String): Int = Int.MAX_VALUE
}
