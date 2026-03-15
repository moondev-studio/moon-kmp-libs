package com.moondeveloper.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SyncResultTest {

    @Test
    fun success_holds_synced_count() {
        val result: SyncResult = SyncResult.Success(syncedCount = 5)
        assertIs<SyncResult.Success>(result)
        assertEquals(5, result.syncedCount)
    }

    @Test
    fun failure_holds_error_and_count() {
        val cause = RuntimeException("network error")
        val result: SyncResult = SyncResult.Failure(error = cause, failedCount = 3)
        assertIs<SyncResult.Failure>(result)
        assertEquals("network error", result.error.message)
        assertEquals(3, result.failedCount)
    }

    @Test
    fun partial_holds_both_counts() {
        val result: SyncResult = SyncResult.Partial(syncedCount = 4, failedCount = 2)
        assertIs<SyncResult.Partial>(result)
        assertEquals(4, result.syncedCount)
        assertEquals(2, result.failedCount)
    }

    @Test
    fun success_zero_is_valid() {
        val result = SyncResult.Success(syncedCount = 0)
        assertEquals(0, result.syncedCount)
    }
}
