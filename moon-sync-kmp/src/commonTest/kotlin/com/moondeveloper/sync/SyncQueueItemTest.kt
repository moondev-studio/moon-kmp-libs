package com.moondeveloper.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SyncQueueItemTest {

    @Test
    fun item_defaults() {
        val item = SyncQueueItem(
            id = "sync_1",
            entityType = "settlement",
            entityId = "s-100",
            action = SyncAction.CREATE,
            createdAt = 1000L
        )
        assertEquals("", item.payload)
        assertEquals(0, item.retryCount)
        assertEquals(SyncQueueStatus.PENDING, item.status)
    }

    @Test
    fun syncAction_has_three_variants() {
        val actions = SyncAction.entries
        assertEquals(3, actions.size)
        assertEquals(SyncAction.CREATE, actions[0])
        assertEquals(SyncAction.UPDATE, actions[1])
        assertEquals(SyncAction.DELETE, actions[2])
    }

    @Test
    fun syncQueueStatus_has_three_variants() {
        val statuses = SyncQueueStatus.entries
        assertEquals(3, statuses.size)
        assertEquals(SyncQueueStatus.PENDING, statuses[0])
        assertEquals(SyncQueueStatus.IN_PROGRESS, statuses[1])
        assertEquals(SyncQueueStatus.FAILED, statuses[2])
    }

    @Test
    fun syncState_sealed_variants() {
        assertIs<SyncState>(SyncState.Idle)
        assertIs<SyncState>(SyncState.Syncing)
        assertIs<SyncState>(SyncState.Completed(3))
        assertIs<SyncState>(SyncState.Error("err"))
    }

    @Test
    fun syncState_completed_holds_count() {
        val state = SyncState.Completed(7)
        assertEquals(7, state.count)
    }

    @Test
    fun syncState_error_holds_message() {
        val state = SyncState.Error("timeout")
        assertEquals("timeout", state.message)
    }
}
