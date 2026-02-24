package com.moondeveloper.sync

interface SyncQueueStore {
    suspend fun add(item: SyncQueueItem)
    suspend fun getPending(): List<SyncQueueItem>
    suspend fun pendingCount(): Int
    suspend fun markCompleted(id: String)
    suspend fun markFailed(id: String, incrementRetry: Boolean = true)
    suspend fun deleteByEntity(entityType: String, entityId: String, action: String)
    suspend fun clear()
}

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

enum class SyncQueueStatus { PENDING, IN_PROGRESS, FAILED }
