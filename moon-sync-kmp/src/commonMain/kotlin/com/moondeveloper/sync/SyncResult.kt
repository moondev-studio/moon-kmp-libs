package com.moondeveloper.sync

sealed class SyncResult {
    data class Success(val syncedCount: Int) : SyncResult()
    data class Failure(val error: Throwable, val failedCount: Int) : SyncResult()
    data class Partial(val syncedCount: Int, val failedCount: Int) : SyncResult()
}
