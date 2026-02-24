package com.moondeveloper.sync

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock

class DefaultSyncManager(
    private val remoteStore: RemoteStore,
    private val conflictResolver: ConflictResolver,
    private val networkMonitor: NetworkMonitor,
    private val queueStore: SyncQueueStore,
    private val maxRetries: Int = 3
) : SyncManager {

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    override val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    override val pendingCount: Flow<Int> = flow {
        emit(queueStore.pendingCount())
    }

    override suspend fun enqueue(
        entityType: String,
        entityId: String,
        action: SyncAction,
        payload: String
    ) {
        queueStore.deleteByEntity(entityType, entityId, action.name)
        queueStore.add(
            SyncQueueItem(
                id = generateId(),
                entityType = entityType,
                entityId = entityId,
                action = action,
                payload = payload,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
        )
        if (networkMonitor.isOnline.value) {
            processQueue()
        }
    }

    override suspend fun processQueue() {
        if (!networkMonitor.isOnline.value) return

        _syncState.value = SyncState.Syncing
        var successCount = 0

        val pending = queueStore.getPending()
        for (item in pending) {
            try {
                val data = mapOf(
                    "entityType" to item.entityType,
                    "entityId" to item.entityId,
                    "payload" to item.payload
                )
                when (item.action) {
                    SyncAction.CREATE -> remoteStore.set(item.entityType, item.entityId, data)
                    SyncAction.UPDATE -> remoteStore.update(item.entityType, item.entityId, data)
                    SyncAction.DELETE -> remoteStore.delete(item.entityType, item.entityId)
                }
                queueStore.markCompleted(item.id)
                successCount++
            } catch (_: Exception) {
                if (item.retryCount >= maxRetries - 1) {
                    queueStore.markFailed(item.id, incrementRetry = false)
                } else {
                    queueStore.markFailed(item.id, incrementRetry = true)
                }
            }
        }

        _syncState.value = if (successCount > 0) {
            SyncState.Completed(successCount)
        } else {
            SyncState.Idle
        }
    }

    override suspend fun clearQueue() {
        queueStore.clear()
    }

    private var idCounter = 0L

    private fun generateId(): String =
        "sync_${Clock.System.now().toEpochMilliseconds()}_${idCounter++}"
}
