package com.moondeveloper.wearable

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NoOpImplementationsTest {

    // WearableConnection Tests

    @Test
    fun noOpWearableConnection_isAvailable_returnsFalse() = runTest {
        assertFalse(NoOpWearableConnection.isAvailable())
    }

    @Test
    fun noOpWearableConnection_connectionState_isDisconnected() = runTest {
        val state = NoOpWearableConnection.connectionState.first()
        assertEquals(ConnectionState.Disconnected, state)
    }

    @Test
    fun noOpWearableConnection_discoverDevices_returnsEmpty() = runTest {
        val devices = NoOpWearableConnection.discoverDevices()
        assertTrue(devices.isEmpty())
    }

    @Test
    fun noOpWearableConnection_connect_fails() = runTest {
        val result = NoOpWearableConnection.connect("any-id")
        assertTrue(result is ConnectionResult.Failure)
    }

    @Test
    fun noOpWearableConnection_getCapabilities_returnsEmpty() = runTest {
        val caps = NoOpWearableConnection.getCapabilities()
        assertTrue(caps.isEmpty())
    }

    @Test
    fun noOpWearableConnection_getBatteryLevel_returnsNull() = runTest {
        assertNull(NoOpWearableConnection.getBatteryLevel())
    }

    // MessageClient Tests

    @Test
    fun noOpMessageClient_isAvailable_returnsFalse() = runTest {
        assertFalse(NoOpMessageClient.isAvailable())
    }

    @Test
    fun noOpMessageClient_sendMessage_fails() = runTest {
        val result = NoOpMessageClient.sendMessage("/test", ByteArray(0))
        assertTrue(result is MessageResult.Failure)
        assertEquals(MessageError.NOT_CONNECTED, (result as MessageResult.Failure).error)
    }

    @Test
    fun noOpMessageClient_observeMessages_emitsNothing() = runTest {
        val messages = NoOpMessageClient.observeMessages().toList()
        assertTrue(messages.isEmpty())
    }

    // DataSync Tests

    @Test
    fun noOpDataSync_isAvailable_returnsFalse() = runTest {
        assertFalse(NoOpDataSync.isAvailable())
    }

    @Test
    fun noOpDataSync_getDataItem_returnsNull() = runTest {
        assertNull(NoOpDataSync.getDataItem("/any/path"))
    }

    @Test
    fun noOpDataSync_getDataItems_returnsEmpty() = runTest {
        val items = NoOpDataSync.getDataItems("/any")
        assertTrue(items.isEmpty())
    }

    @Test
    fun noOpDataSync_putDataItem_fails() = runTest {
        val result = NoOpDataSync.putDataItem("/test", ByteArray(0))
        assertTrue(result is DataSyncResult.Failure)
        assertEquals(DataSyncError.NOT_CONNECTED, (result as DataSyncResult.Failure).error)
    }

    @Test
    fun noOpDataSync_deleteDataItems_returnsZero() = runTest {
        val count = NoOpDataSync.deleteDataItems("/any")
        assertEquals(0, count)
    }
}
