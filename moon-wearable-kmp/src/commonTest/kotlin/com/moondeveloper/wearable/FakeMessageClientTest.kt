package com.moondeveloper.wearable

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeMessageClientTest {

    private val client = FakeMessageClient()

    @Test
    fun isAvailable_returnsConfiguredValue() = runTest {
        client.isAvailableValue = true
        assertTrue(client.isAvailable())

        client.isAvailableValue = false
        assertEquals(false, client.isAvailable())
    }

    @Test
    fun sendMessage_recordsMessage() = runTest {
        val result = client.sendMessage("/test/path", "Hello".encodeToByteArray())

        assertEquals(MessageResult.Success, result)
        assertEquals(1, client.sentMessages.size)
        assertEquals("/test/path", client.sentMessages[0].path)
        assertEquals("Hello", client.sentMessages[0].dataAsText())
    }

    @Test
    fun sendTextMessage_recordsTextMessage() = runTest {
        val result = client.sendTextMessage("/chat/message", "Hello World")

        assertEquals(MessageResult.Success, result)
        assertEquals("Hello World", client.sentMessages[0].dataAsText())
    }

    @Test
    fun sendMessage_returnsError_whenSimulated() = runTest {
        client.simulateError = MessageError.NOT_CONNECTED

        val result = client.sendMessage("/test/path", ByteArray(0))

        assertTrue(result is MessageResult.Failure)
        assertEquals(MessageError.NOT_CONNECTED, (result as MessageResult.Failure).error)
    }

    @Test
    fun observeMessages_receivesSimulatedMessages() = runTest {
        val received = mutableListOf<WearableMessage>()

        val job = launch {
            client.observeMessages().collect { received.add(it) }
        }
        yield() // Allow collector to start

        val message = WearableMessage("/test/path", "Test".encodeToByteArray(), "device-1")
        client.receiveMessage(message)
        yield() // Allow message to be processed

        assertEquals(1, received.size)
        assertEquals("/test/path", received[0].path)

        job.cancel()
    }

    @Test
    fun observeMessages_filtersbyPath() = runTest {
        val received = mutableListOf<WearableMessage>()

        val job = launch {
            client.observeMessages("/sync").collect { received.add(it) }
        }
        yield() // Allow collector to start

        client.receiveMessage(WearableMessage("/sync/start", ByteArray(0), "device-1"))
        yield()
        client.receiveMessage(WearableMessage("/chat/message", ByteArray(0), "device-1"))
        yield()
        client.receiveMessage(WearableMessage("/sync/complete", ByteArray(0), "device-1"))
        yield() // Allow messages to be processed

        assertEquals(2, received.size)
        assertTrue(received.all { it.path.startsWith("/sync") })

        job.cancel()
    }

    @Test
    fun clear_resetsSentMessages() = runTest {
        client.sendMessage("/test", ByteArray(0))
        client.simulateError = MessageError.TIMEOUT

        client.clear()

        assertTrue(client.sentMessages.isEmpty())

        val result = client.sendMessage("/test2", ByteArray(0))
        assertEquals(MessageResult.Success, result)
    }

    @Test
    fun wearableMessage_dataAsText_decodesCorrectly() {
        val message = WearableMessage(
            path = "/test",
            data = "Hello World".encodeToByteArray(),
            sourceDeviceId = "device-1"
        )

        assertEquals("Hello World", message.dataAsText())
    }
}
