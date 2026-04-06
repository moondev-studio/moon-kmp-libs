package com.moondeveloper.wearable

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeDataSyncTest {

    private val dataSync = FakeDataSync()

    @Test
    fun isAvailable_returnsConfiguredValue() = runTest {
        dataSync.isAvailableValue = true
        assertTrue(dataSync.isAvailable())

        dataSync.isAvailableValue = false
        assertEquals(false, dataSync.isAvailable())
    }

    @Test
    fun putDataItem_storesData() = runTest {
        val result = dataSync.putDataItem("/settings/theme", "dark".encodeToByteArray())

        assertEquals(DataSyncResult.Success, result)

        val item = dataSync.getDataItem("/settings/theme")
        assertEquals("dark", item?.dataAsString())
    }

    @Test
    fun putStringItem_storesString() = runTest {
        dataSync.putStringItem("/user/name", "John")

        val item = dataSync.getDataItem("/user/name")
        assertEquals("John", item?.dataAsString())
    }

    @Test
    fun getDataItem_returnsNullForNonexistent() = runTest {
        val item = dataSync.getDataItem("/nonexistent")
        assertNull(item)
    }

    @Test
    fun getDataItems_returnsMatchingPrefix() = runTest {
        dataSync.putStringItem("/settings/theme", "dark")
        dataSync.putStringItem("/settings/lang", "en")
        dataSync.putStringItem("/user/name", "John")

        val items = dataSync.getDataItems("/settings")

        assertEquals(2, items.size)
        assertTrue(items.all { it.path.startsWith("/settings") })
    }

    @Test
    fun deleteDataItem_removesItem() = runTest {
        dataSync.putStringItem("/temp/data", "value")

        val result = dataSync.deleteDataItem("/temp/data")

        assertEquals(DataSyncResult.Success, result)
        assertNull(dataSync.getDataItem("/temp/data"))
    }

    @Test
    fun deleteDataItems_removesMultipleItems() = runTest {
        dataSync.putStringItem("/temp/a", "1")
        dataSync.putStringItem("/temp/b", "2")
        dataSync.putStringItem("/keep/c", "3")

        val count = dataSync.deleteDataItems("/temp")

        assertEquals(2, count)
        assertNull(dataSync.getDataItem("/temp/a"))
        assertNull(dataSync.getDataItem("/temp/b"))
        assertEquals("3", dataSync.getDataItem("/keep/c")?.dataAsString())
    }

    @Test
    fun observeDataItem_emitsChanges() = runTest {
        dataSync.putStringItem("/watch/status", "initial")

        val initial = dataSync.observeDataItem("/watch/status").first()
        assertEquals("initial", initial?.dataAsString())

        dataSync.putStringItem("/watch/status", "updated")

        val updated = dataSync.observeDataItem("/watch/status").first()
        assertEquals("updated", updated?.dataAsString())
    }

    @Test
    fun putDataItem_returnsError_whenSimulated() = runTest {
        dataSync.simulateError = DataSyncError.PAYLOAD_TOO_LARGE

        val result = dataSync.putDataItem("/test", ByteArray(0))

        assertTrue(result is DataSyncResult.Failure)
        assertEquals(DataSyncError.PAYLOAD_TOO_LARGE, (result as DataSyncResult.Failure).error)
    }

    @Test
    fun addItem_prePopulatesData() = runTest {
        dataSync.addStringItem("/preset/value", "preset-data")

        val item = dataSync.getDataItem("/preset/value")
        assertEquals("preset-data", item?.dataAsString())
    }

    @Test
    fun clear_removesAllData() = runTest {
        dataSync.putStringItem("/a", "1")
        dataSync.putStringItem("/b", "2")
        dataSync.simulateError = DataSyncError.UNKNOWN

        dataSync.clear()

        assertTrue(dataSync.allItems.isEmpty())
        assertEquals(DataSyncResult.Success, dataSync.putDataItem("/new", ByteArray(0)))
    }
}
