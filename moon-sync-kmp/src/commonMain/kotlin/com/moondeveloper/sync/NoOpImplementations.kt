package com.moondeveloper.sync

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

/** No-op [NetworkMonitor] that always reports online. */
object NoOpNetworkMonitor : NetworkMonitor {
    override val isOnline: StateFlow<Boolean> = MutableStateFlow(true)
}

/** No-op [SyncManager] that discards all enqueued items. */
object NoOpSyncManager : SyncManager {
    override val pendingCount: Flow<Int> = flowOf(0)
    override val syncState: StateFlow<SyncState> = MutableStateFlow(SyncState.Idle)
    override suspend fun enqueue(entityType: String, entityId: String, action: SyncAction, payload: String) {}
    override suspend fun processQueue() {}
    override suspend fun clearQueue() {}
}
