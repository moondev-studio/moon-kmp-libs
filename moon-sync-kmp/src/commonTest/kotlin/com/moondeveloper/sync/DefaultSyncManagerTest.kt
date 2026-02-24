package com.moondeveloper.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultSyncManagerTest {

    @Test
    fun enqueue_when_online_syncs_immediately() = runTest {
        val network = FakeNetworkMonitor(online = true)
        val remote = FakeRemoteStore()
        val queue = FakeSyncQueueStore()
        val manager = DefaultSyncManager(remote, ServerWinsConflictResolver(), network, queue)

        manager.enqueue("settlements", "s1", SyncAction.CREATE, """{"title":"test"}""")

        // Item should have been synced and removed from pending
        assertEquals(0, queue.pendingCount())
        // Remote should have received the set call
        assertEquals(1, remote.setCalls.size)
        assertEquals("settlements", remote.setCalls[0].collection)
        assertEquals("s1", remote.setCalls[0].documentId)
        assertEquals(SyncState.Completed(1), manager.syncState.value)
    }

    @Test
    fun enqueue_when_offline_queues_only() = runTest {
        val network = FakeNetworkMonitor(online = false)
        val remote = FakeRemoteStore()
        val queue = FakeSyncQueueStore()
        val manager = DefaultSyncManager(remote, ServerWinsConflictResolver(), network, queue)

        manager.enqueue("settlements", "s1", SyncAction.CREATE, """{"title":"test"}""")

        // Item should remain in queue
        assertEquals(1, queue.pendingCount())
        // Remote should NOT have been called
        assertEquals(0, remote.setCalls.size)
        assertEquals(SyncState.Idle, manager.syncState.value)
    }

    @Test
    fun processQueue_processes_all_pending_operations() = runTest {
        val network = FakeNetworkMonitor(online = false)
        val remote = FakeRemoteStore()
        val queue = FakeSyncQueueStore()
        val manager = DefaultSyncManager(remote, ServerWinsConflictResolver(), network, queue)

        // Enqueue 3 items while offline
        manager.enqueue("settlements", "s1", SyncAction.CREATE)
        manager.enqueue("settlements", "s2", SyncAction.UPDATE)
        manager.enqueue("ledger", "l1", SyncAction.DELETE)

        assertEquals(3, queue.pendingCount())

        // Go online and process
        network.setOnline(true)
        manager.processQueue()

        assertEquals(0, queue.pendingCount())
        assertEquals(1, remote.setCalls.size)
        assertEquals(1, remote.updateCalls.size)
        assertEquals(1, remote.deleteCalls.size)
        assertEquals(SyncState.Completed(3), manager.syncState.value)
    }

    @Test
    fun processQueue_when_offline_does_nothing() = runTest {
        val network = FakeNetworkMonitor(online = false)
        val remote = FakeRemoteStore()
        val queue = FakeSyncQueueStore()
        val manager = DefaultSyncManager(remote, ServerWinsConflictResolver(), network, queue)

        // Add item directly to queue
        queue.add(
            SyncQueueItem(
                id = "test1",
                entityType = "settlements",
                entityId = "s1",
                action = SyncAction.CREATE,
                createdAt = 1000L
            )
        )

        manager.processQueue()

        assertEquals(1, queue.pendingCount())
        assertEquals(0, remote.setCalls.size)
    }

    @Test
    fun partial_failure_handling() = runTest {
        val network = FakeNetworkMonitor(online = true)
        val remote = FakeRemoteStore()
        val queue = FakeSyncQueueStore()
        val manager = DefaultSyncManager(remote, ServerWinsConflictResolver(), network, queue, maxRetries = 2)

        // Make remote fail for specific entity
        remote.failOnSet.add("s2")

        // Add items directly to avoid auto-sync deduplication
        network.setOnline(false)
        manager.enqueue("settlements", "s1", SyncAction.CREATE)
        manager.enqueue("settlements", "s2", SyncAction.CREATE)
        manager.enqueue("settlements", "s3", SyncAction.CREATE)

        network.setOnline(true)
        manager.processQueue()

        // s1 and s3 should succeed, s2 should fail
        assertEquals(1, queue.pendingCount()) // s2 still pending (retry incremented)
        assertEquals(SyncState.Completed(2), manager.syncState.value)

        // The failed item should have retry incremented
        val remaining = queue.getPending()
        assertEquals(1, remaining.size)
        assertEquals("s2", remaining[0].entityId)
        assertEquals(1, remaining[0].retryCount)
    }

    @Test
    fun max_retries_exceeded_marks_failed() = runTest {
        val network = FakeNetworkMonitor(online = true)
        val remote = FakeRemoteStore()
        val queue = FakeSyncQueueStore()
        val manager = DefaultSyncManager(remote, ServerWinsConflictResolver(), network, queue, maxRetries = 2)

        remote.failOnSet.add("s1")

        // Add item at retry limit (retryCount = 1, maxRetries = 2)
        queue.add(
            SyncQueueItem(
                id = "test1",
                entityType = "settlements",
                entityId = "s1",
                action = SyncAction.CREATE,
                createdAt = 1000L,
                retryCount = 1 // Already retried once, maxRetries-1 = 1
            )
        )

        manager.processQueue()

        // Item should be marked FAILED (not pending anymore)
        assertEquals(0, queue.pendingCount())
        val failed = queue.allItems.filter { it.status == SyncQueueStatus.FAILED }
        assertEquals(1, failed.size)
    }

    @Test
    fun conflict_resolver_server_wins() = runTest {
        val resolver = ServerWinsConflictResolver()
        val local = mapOf("title" to "local version", "amount" to 100)
        val remote = mapOf("title" to "server version", "amount" to 200)

        val result: ConflictResolution = resolver.resolve(local, remote)

        val useRemote = result as ConflictResolution.UseRemote
        assertEquals("server version", useRemote.data["title"])
        assertEquals(200, useRemote.data["amount"])
    }

    @Test
    fun deduplication_removes_existing_before_adding() = runTest {
        val network = FakeNetworkMonitor(online = false)
        val remote = FakeRemoteStore()
        val queue = FakeSyncQueueStore()
        val manager = DefaultSyncManager(remote, ServerWinsConflictResolver(), network, queue)

        // Enqueue same entity twice
        manager.enqueue("settlements", "s1", SyncAction.UPDATE, "first")
        manager.enqueue("settlements", "s1", SyncAction.UPDATE, "second")

        // Should only have one item (deduplicated)
        assertEquals(1, queue.pendingCount())
        val items = queue.getPending()
        assertEquals("second", items[0].payload)
    }

    @Test
    fun clearQueue_removes_everything() = runTest {
        val network = FakeNetworkMonitor(online = false)
        val remote = FakeRemoteStore()
        val queue = FakeSyncQueueStore()
        val manager = DefaultSyncManager(remote, ServerWinsConflictResolver(), network, queue)

        manager.enqueue("settlements", "s1", SyncAction.CREATE)
        manager.enqueue("settlements", "s2", SyncAction.UPDATE)
        assertEquals(2, queue.pendingCount())

        manager.clearQueue()

        assertEquals(0, queue.pendingCount())
    }

    @Test
    fun noOp_implementations_do_not_crash() = runTest {
        // NoOpNetworkMonitor
        assertTrue(NoOpNetworkMonitor.isOnline.value)

        // NoOpSyncManager
        assertEquals(0, NoOpSyncManager.pendingCount.first())
        assertEquals(SyncState.Idle, NoOpSyncManager.syncState.value)
        NoOpSyncManager.enqueue("test", "t1", SyncAction.CREATE)
        NoOpSyncManager.processQueue()
        NoOpSyncManager.clearQueue()
    }

    @Test
    fun delete_action_calls_remote_delete() = runTest {
        val network = FakeNetworkMonitor(online = true)
        val remote = FakeRemoteStore()
        val queue = FakeSyncQueueStore()
        val manager = DefaultSyncManager(remote, ServerWinsConflictResolver(), network, queue)

        manager.enqueue("settlements", "s1", SyncAction.DELETE)

        assertEquals(1, remote.deleteCalls.size)
        assertEquals("settlements", remote.deleteCalls[0].collection)
        assertEquals("s1", remote.deleteCalls[0].documentId)
    }
}

// --- Test Fakes ---

private class FakeNetworkMonitor(online: Boolean = true) : NetworkMonitor {
    private val _isOnline = MutableStateFlow(online)
    override val isOnline: StateFlow<Boolean> = _isOnline

    fun setOnline(value: Boolean) {
        _isOnline.value = value
    }
}

private class FakeRemoteStore : RemoteStore {
    data class SetCall(val collection: String, val documentId: String, val data: Map<String, Any?>)
    data class UpdateCall(val collection: String, val documentId: String, val data: Map<String, Any?>)
    data class DeleteCall(val collection: String, val documentId: String)

    val setCalls = mutableListOf<SetCall>()
    val updateCalls = mutableListOf<UpdateCall>()
    val deleteCalls = mutableListOf<DeleteCall>()
    val failOnSet = mutableSetOf<String>() // documentIds that should fail

    private val store = mutableMapOf<String, MutableMap<String, Map<String, Any?>>>()

    override suspend fun get(collection: String, documentId: String): Map<String, Any?>? {
        return store[collection]?.get(documentId)
    }

    override suspend fun set(collection: String, documentId: String, data: Map<String, Any?>) {
        if (documentId in failOnSet) throw RuntimeException("Simulated failure for $documentId")
        setCalls.add(SetCall(collection, documentId, data))
        store.getOrPut(collection) { mutableMapOf() }[documentId] = data
    }

    override suspend fun update(collection: String, documentId: String, data: Map<String, Any?>) {
        updateCalls.add(UpdateCall(collection, documentId, data))
        val existing = store.getOrPut(collection) { mutableMapOf() }.getOrPut(documentId) { emptyMap() }
        store[collection]!![documentId] = existing + data
    }

    override suspend fun delete(collection: String, documentId: String) {
        deleteCalls.add(DeleteCall(collection, documentId))
        store[collection]?.remove(documentId)
    }

    override suspend fun query(collection: String, filters: List<QueryFilter>): List<Map<String, Any?>> {
        return store[collection]?.values?.toList() ?: emptyList()
    }
}

private class FakeSyncQueueStore : SyncQueueStore {
    val allItems = mutableListOf<SyncQueueItem>()

    override suspend fun add(item: SyncQueueItem) {
        allItems.add(item)
    }

    override suspend fun getPending(): List<SyncQueueItem> {
        return allItems.filter { it.status == SyncQueueStatus.PENDING }
    }

    override suspend fun pendingCount(): Int {
        return allItems.count { it.status == SyncQueueStatus.PENDING }
    }

    override suspend fun markCompleted(id: String) {
        val index = allItems.indexOfFirst { it.id == id }
        if (index >= 0) {
            allItems.removeAt(index) // completed items are removed
        }
    }

    override suspend fun markFailed(id: String, incrementRetry: Boolean) {
        val index = allItems.indexOfFirst { it.id == id }
        if (index >= 0) {
            val item = allItems[index]
            val newRetry = if (incrementRetry) item.retryCount + 1 else item.retryCount
            allItems[index] = item.copy(
                retryCount = newRetry,
                status = if (!incrementRetry) SyncQueueStatus.FAILED else SyncQueueStatus.PENDING
            )
        }
    }

    override suspend fun deleteByEntity(entityType: String, entityId: String, action: String) {
        allItems.removeAll { it.entityType == entityType && it.entityId == entityId && it.action.name == action }
    }

    override suspend fun clear() {
        allItems.clear()
    }
}
