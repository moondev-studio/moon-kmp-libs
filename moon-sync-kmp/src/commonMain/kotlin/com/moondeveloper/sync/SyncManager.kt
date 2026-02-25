package com.moondeveloper.sync

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Offline-first sync manager.
 *
 * Enqueues local changes and syncs them to a [RemoteStore] when the device is online.
 *
 * @see DefaultSyncManager for the default implementation
 * @see NoOpSyncManager for testing
 */
interface SyncManager {
    /** Number of pending sync items. */
    val pendingCount: Flow<Int>

    /** Current sync state. */
    val syncState: StateFlow<SyncState>

    /** Enqueue a change for sync. Auto-syncs if online. */
    suspend fun enqueue(entityType: String, entityId: String, action: SyncAction, payload: String = "")

    /** Process all pending items in the queue. */
    suspend fun processQueue()

    /** Clear all pending items from the queue. */
    suspend fun clearQueue()
}

/** Type of sync operation. */
enum class SyncAction { CREATE, UPDATE, DELETE }

/** Current state of the sync engine. */
sealed class SyncState {
    data object Idle : SyncState()
    data object Syncing : SyncState()
    data class Completed(val count: Int) : SyncState()
    data class Error(val message: String) : SyncState()
}
