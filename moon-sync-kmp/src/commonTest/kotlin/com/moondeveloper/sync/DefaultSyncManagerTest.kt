package com.moondeveloper.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DefaultSyncManagerTest {

    private class FakeQueueStore : SyncQueueStore {
        val items = mutableListOf<SyncQueueItem>()

        override suspend fun add(item: SyncQueueItem) { items.add(item) }
        override suspend fun getPending() = items.filter { it.status == SyncQueueStatus.PENDING }
        override suspend fun pendingCount() = items.count { it.status == SyncQueueStatus.PENDING }
        override suspend fun markCompleted(id: String) { items.removeAll { it.id == id } }
        override suspend fun markFailed(id: String, incrementRetry: Boolean) {
            val idx = items.indexOfFirst { it.id == id }
            if (idx >= 0) {
                val old = items[idx]
                items[idx] = old.copy(
                    status = SyncQueueStatus.FAILED,
                    retryCount = if (incrementRetry) old.retryCount + 1 else old.retryCount
                )
            }
        }
        override suspend fun deleteByEntity(entityType: String, entityId: String, action: String) {
            items.removeAll { it.entityType == entityType && it.entityId == entityId && it.action.name == action }
        }
        override suspend fun clear() { items.clear() }
    }

    private class FakeRemoteStore : RemoteStore {
        val docs = mutableMapOf<String, MutableMap<String, Map<String, Any?>>>()

        override suspend fun get(collection: String, documentId: String) =
            docs[collection]?.get(documentId)

        override suspend fun set(collection: String, documentId: String, data: Map<String, Any?>) {
            docs.getOrPut(collection) { mutableMapOf() }[documentId] = data
        }

        override suspend fun update(collection: String, documentId: String, data: Map<String, Any?>) {
            val existing = docs[collection]?.get(documentId) ?: return
            docs[collection]!![documentId] = existing + data
        }

        override suspend fun delete(collection: String, documentId: String) {
            docs[collection]?.remove(documentId)
        }

        override suspend fun query(collection: String, filters: List<QueryFilter>) =
            docs[collection]?.values?.toList() ?: emptyList()
    }

    private class FakeNetworkMonitor(online: Boolean = true) : NetworkMonitor {
        override val isOnline: StateFlow<Boolean> = MutableStateFlow(online)
    }

    private fun createManager(
        remoteStore: FakeRemoteStore = FakeRemoteStore(),
        queueStore: FakeQueueStore = FakeQueueStore(),
        online: Boolean = true,
    ) = DefaultSyncManager(
        remoteStore = remoteStore,
        conflictResolver = ServerWinsConflictResolver(),
        networkMonitor = FakeNetworkMonitor(online),
        queueStore = queueStore,
    )

    @Test
    fun initial_state_is_idle() {
        val manager = createManager()
        assertIs<SyncState.Idle>(manager.syncState.value)
        assertEquals(0, manager.pendingCount.value)
    }

    @Test
    fun enqueue_creates_item_in_queue() = runTest {
        val queue = FakeQueueStore()
        val remote = FakeRemoteStore()
        val manager = createManager(remoteStore = remote, queueStore = queue)

        manager.enqueue("settlement", "s-1", SyncAction.CREATE, "{}")

        // processQueue runs automatically if online, item is synced to remote
        assertEquals("settlement", remote.docs["settlement"]?.get("s-1")?.get("entityType"))
    }

    @Test
    fun processQueue_offline_returns_failure() = runTest {
        val manager = createManager(online = false)
        val result = manager.processQueueWithResult()

        assertIs<SyncResult.Failure>(result)
    }

    @Test
    fun processQueue_empty_returns_success_zero() = runTest {
        val manager = createManager()
        val result = manager.processQueueWithResult()

        assertIs<SyncResult.Success>(result)
        assertEquals(0, result.syncedCount)
    }

    @Test
    fun clearQueue_resets_pending_count() = runTest {
        val queue = FakeQueueStore()
        val manager = createManager(queueStore = queue, online = false)

        manager.enqueue("type", "id-1", SyncAction.CREATE)
        manager.clearQueue()

        assertEquals(0, manager.pendingCount.value)
    }
}
