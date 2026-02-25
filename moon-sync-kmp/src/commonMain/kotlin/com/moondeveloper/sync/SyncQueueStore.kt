package com.moondeveloper.sync

/**
 * Persistent storage for the sync queue.
 *
 * Implementations should persist items across app restarts (e.g., Room, SQLite).
 */
interface SyncQueueStore {
    suspend fun add(item: SyncQueueItem)
    suspend fun getPending(): List<SyncQueueItem>
    suspend fun pendingCount(): Int
    suspend fun markCompleted(id: String)
    suspend fun markFailed(id: String, incrementRetry: Boolean = true)
    suspend fun deleteByEntity(entityType: String, entityId: String, action: String)
    suspend fun clear()
}

/** A queued sync operation. */
data class SyncQueueItem(
    val id: String,
    val entityType: String,
    val entityId: String,
    val action: SyncAction,
    val payload: String = "",
    val createdAt: Long,
    val retryCount: Int = 0,
    val status: SyncQueueStatus = SyncQueueStatus.PENDING
)

/** Processing status of a [SyncQueueItem]. */
enum class SyncQueueStatus { PENDING, IN_PROGRESS, FAILED }
