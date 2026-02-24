package com.moondeveloper.billing

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UsageLimitCheckerTest {

    @Test
    fun noOp_canUse_returns_allowed() = runTest {
        val result = NoOpUsageLimitChecker.canUse("settlement")
        assertIs<LimitCheckResult.Allowed>(result)
    }

    @Test
    fun noOp_getRemainingCount_returns_max() = runTest {
        assertEquals(Int.MAX_VALUE, NoOpUsageLimitChecker.getRemainingCount("settlement"))
    }

    @Test
    fun noOp_recordUsage_does_not_throw() = runTest {
        NoOpUsageLimitChecker.recordUsage("settlement")
        // Should still be Allowed after recording
        assertIs<LimitCheckResult.Allowed>(NoOpUsageLimitChecker.canUse("settlement"))
    }
}
