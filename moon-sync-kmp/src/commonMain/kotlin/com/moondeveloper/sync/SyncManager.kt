package com.moondeveloper.sync

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SyncManager {
    val pendingCount: Flow<Int>
    val syncState: StateFlow<SyncState>
    suspend fun enqueue(entityType: String, entityId: String, action: SyncAction, payload: String = "")
    suspend fun processQueue()
    suspend fun clearQueue()
}

enum class SyncAction { CREATE, UPDATE, DELETE }

sealed class SyncState {
    data object Idle : SyncState()
    data object Syncing : SyncState()
    data class Completed(val count: Int) : SyncState()
    data class Error(val message: String) : SyncState()
}
