package com.moondeveloper.wearable

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

/**
 * Fake [DataSync] for unit testing.
 */
class FakeDataSync : DataSync {

    private val dataItems = MutableStateFlow<Map<String, DataItem>>(emptyMap())

    var isAvailableValue = true
    var simulateError: DataSyncError? = null

    /** All current data items */
    val allItems: List<DataItem> get() = dataItems.value.values.toList()

    /** Clear all data */
    fun clear() {
        dataItems.value = emptyMap()
        simulateError = null
    }

    /** Pre-populate a data item */
    fun addItem(path: String, data: ByteArray) {
        val current = dataItems.value.toMutableMap()
        current[path] = DataItem(path, data, Clock.System.now().toEpochMilliseconds())
        dataItems.value = current
    }

    /** Pre-populate a string data item */
    fun addStringItem(path: String, value: String) {
        addItem(path, value.encodeToByteArray())
    }

    override fun observeDataItem(path: String): Flow<DataItem?> =
        dataItems.map { it[path] }

    override fun observeDataItems(pathPrefix: String): Flow<List<DataItem>> =
        dataItems.map { items ->
            items.filter { (key, _) -> key.startsWith(pathPrefix) }.values.toList()
        }

    override suspend fun getDataItem(path: String): DataItem? = dataItems.value[path]

    override suspend fun getDataItems(pathPrefix: String): List<DataItem> =
        dataItems.value.filter { (key, _) -> key.startsWith(pathPrefix) }.values.toList()

    override suspend fun putDataItem(
        path: String,
        data: ByteArray,
        urgent: Boolean
    ): DataSyncResult {
        simulateError?.let { error ->
            return DataSyncResult.Failure(error)
        }

        val current = dataItems.value.toMutableMap()
        current[path] = DataItem(path, data, Clock.System.now().toEpochMilliseconds())
        dataItems.value = current
        return DataSyncResult.Success
    }

    override suspend fun deleteDataItem(path: String): DataSyncResult {
        simulateError?.let { error ->
            return DataSyncResult.Failure(error)
        }

        val current = dataItems.value.toMutableMap()
        current.remove(path)
        dataItems.value = current
        return DataSyncResult.Success
    }

    override suspend fun deleteDataItems(pathPrefix: String): Int {
        val current = dataItems.value.toMutableMap()
        val toDelete = current.keys.filter { it.startsWith(pathPrefix) }
        toDelete.forEach { current.remove(it) }
        dataItems.value = current
        return toDelete.size
    }

    override suspend fun isAvailable(): Boolean = isAvailableValue
}
