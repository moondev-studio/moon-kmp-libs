package com.moondeveloper.sync

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class NoOpImplementationsTest {

    @Test
    fun noOpNetworkMonitor_always_online() {
        assertTrue(NoOpNetworkMonitor.isOnline.value)
    }

    @Test
    fun noOpSyncManager_pendingCount_is_zero() {
        assertEquals(0, NoOpSyncManager.pendingCount.value)
    }

    @Test
    fun noOpSyncManager_syncState_is_idle() {
        assertIs<SyncState.Idle>(NoOpSyncManager.syncState.value)
    }

    @Test
    fun noOpSyncManager_enqueue_does_nothing() = runTest {
        NoOpSyncManager.enqueue("type", "id", SyncAction.CREATE, "payload")
        assertEquals(0, NoOpSyncManager.pendingCount.value)
    }

    @Test
    fun noOpSyncManager_processQueue_does_nothing() = runTest {
        NoOpSyncManager.processQueue()
        assertIs<SyncState.Idle>(NoOpSyncManager.syncState.value)
    }

    @Test
    fun noOpSyncManager_clearQueue_does_nothing() = runTest {
        NoOpSyncManager.clearQueue()
        assertEquals(0, NoOpSyncManager.pendingCount.value)
    }
}
