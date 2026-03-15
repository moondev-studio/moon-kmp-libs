package com.moondeveloper.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Clock

/**
 * Default [SyncManager] implementation with queue-based sync, conflict resolution, and automatic retry.
 *
 * Automatically syncs when items are enqueued and the device is online.
 * Failed items are retried up to [maxRetries] times.
 *
 * On UPDATE actions, fetches remote data and delegates to [conflictResolver] when both
 * local and remote versions exist. The resolved data is then pushed to the remote store.
 *
 * @param remoteStore Backend data store for sync operations
 * @param conflictResolver Strategy for resolving local/remote conflicts
 * @param networkMonitor Monitors network connectivity
 * @param queueStore Persistent queue storage
 * @param maxRetries Maximum retry attempts per item (default: 3)
 */
class DefaultSyncManager(
    private val remoteStore: RemoteStore,
    private val conflictResolver: ConflictResolver,
    private val networkMonitor: NetworkMonitor,
    private val queueStore: SyncQueueStore,
    private val maxRetries: Int = 3
) : SyncManager {

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    override val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _pendingCount = MutableStateFlow(0)
    override val pendingCount: StateFlow<Int> = _pendingCount.asStateFlow()

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
        refreshPendingCount()
        if (networkMonitor.isOnline.value) {
            processQueue()
        }
    }

    /**
     * Process all pending items in the queue.
     *
     * Returns a [SyncResult] indicating the outcome:
     * - [SyncResult.Success] if all items were synced
     * - [SyncResult.Partial] if some items succeeded and some failed
     * - [SyncResult.Failure] if no items could be synced
     */
    suspend fun processQueueWithResult(): SyncResult {
        if (!networkMonitor.isOnline.value) return SyncResult.Failure(
            error = IllegalStateException("Device is offline"),
            failedCount = 0
        )

        _syncState.value = SyncState.Syncing
        var successCount = 0
        var failCount = 0

        val pending = queueStore.getPending()
        for (item in pending) {
            try {
                processItem(item)
                queueStore.markCompleted(item.id)
                successCount++
            } catch (e: Exception) {
                failCount++
                if (item.retryCount >= maxRetries - 1) {
                    queueStore.markFailed(item.id, incrementRetry = false)
                } else {
                    queueStore.markFailed(item.id, incrementRetry = true)
                }
            }
        }

        refreshPendingCount()

        val result = when {
            failCount == 0 && successCount > 0 -> SyncResult.Success(successCount)
            successCount > 0 && failCount > 0 -> SyncResult.Partial(successCount, failCount)
            failCount > 0 -> SyncResult.Failure(
                error = RuntimeException("All $failCount items failed to sync"),
                failedCount = failCount
            )
            else -> SyncResult.Success(0)
        }

        _syncState.value = when (result) {
            is SyncResult.Success -> if (result.syncedCount > 0) SyncState.Completed(result.syncedCount) else SyncState.Idle
            is SyncResult.Partial -> SyncState.Completed(result.syncedCount)
            is SyncResult.Failure -> SyncState.Error("Sync failed: ${result.failedCount} items")
        }

        return result
    }

    override suspend fun processQueue() {
        processQueueWithResult()
    }

    override suspend fun clearQueue() {
        queueStore.clear()
        refreshPendingCount()
    }

    /**
     * Process a single sync queue item.
     * For UPDATE actions, resolves conflicts via [conflictResolver] when remote data exists.
     */
    private suspend fun processItem(item: SyncQueueItem) {
        val localData = mapOf(
            "entityType" to item.entityType,
            "entityId" to item.entityId,
            "payload" to item.payload
        )
        when (item.action) {
            SyncAction.CREATE -> remoteStore.set(item.entityType, item.entityId, localData)
            SyncAction.UPDATE -> {
                val remoteData = remoteStore.get(item.entityType, item.entityId)
                if (remoteData != null) {
                    // Conflict detected — delegate to resolver
                    val resolution = conflictResolver.resolve(local = localData, remote = remoteData)
                    val resolved = when (resolution) {
                        is ConflictResolution.UseLocal -> resolution.data
                        is ConflictResolution.UseRemote -> resolution.data
                        is ConflictResolution.Merged -> resolution.data
                        is ConflictResolution.RequireUserInput -> {
                            // Fall back to local data when user input is required but not available
                            localData
                        }
                    }
                    remoteStore.update(item.entityType, item.entityId, resolved)
                } else {
                    // No remote data exists — treat as fresh write
                    remoteStore.set(item.entityType, item.entityId, localData)
                }
            }
            SyncAction.DELETE -> remoteStore.delete(item.entityType, item.entityId)
        }
    }

    private suspend fun refreshPendingCount() {
        _pendingCount.value = queueStore.pendingCount()
    }

    private var idCounter = 0L

    private fun generateId(): String =
        "sync_${Clock.System.now().toEpochMilliseconds()}_${idCounter++}"
}
