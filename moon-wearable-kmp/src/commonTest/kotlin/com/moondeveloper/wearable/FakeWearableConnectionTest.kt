package com.moondeveloper.wearable

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeWearableConnectionTest {

    private val connection = FakeWearableConnection()

    private val testDevice = WearableDevice(
        id = "test-watch-1",
        name = "Test Watch",
        type = WearableType.WATCH,
        manufacturer = "Test Co",
        model = "Model X"
    )

    @Test
    fun isAvailable_returnsConfiguredValue() = runTest {
        connection.isAvailableValue = true
        assertTrue(connection.isAvailable())

        connection.isAvailableValue = false
        assertFalse(connection.isAvailable())
    }

    @Test
    fun connectionState_startsDisconnected() = runTest {
        val state = connection.connectionState.first()
        assertEquals(ConnectionState.Disconnected, state)
    }

    @Test
    fun discoverDevices_returnsAddedDevices() = runTest {
        connection.addDevice(testDevice)

        val devices = connection.discoverDevices()

        assertEquals(1, devices.size)
        assertEquals("test-watch-1", devices[0].id)
    }

    @Test
    fun connect_succeeds_whenDeviceExists() = runTest {
        connection.addDevice(testDevice)

        val result = connection.connect("test-watch-1")

        assertTrue(result is ConnectionResult.Success)
        assertEquals(testDevice, (result as ConnectionResult.Success).device)

        val state = connection.connectionState.first()
        assertTrue(state is ConnectionState.Connected)
    }

    @Test
    fun connect_fails_whenDeviceNotFound() = runTest {
        val result = connection.connect("unknown-device")

        assertTrue(result is ConnectionResult.Failure)
        assertEquals(ConnectionError.DEVICE_NOT_FOUND, (result as ConnectionResult.Failure).error)
    }

    @Test
    fun disconnect_setsStateToDisconnected() = runTest {
        connection.addDevice(testDevice)
        connection.connect("test-watch-1")

        connection.disconnect()

        val state = connection.connectionState.first()
        assertEquals(ConnectionState.Disconnected, state)
        assertNull(connection.getConnectedDevice())
    }

    @Test
    fun getCapabilities_returnsConfiguredCapabilities() = runTest {
        val capabilities = setOf(
            WearableCapability.HEART_RATE,
            WearableCapability.STEPS,
            WearableCapability.SEND_MESSAGES
        )
        connection.addDevice(testDevice, capabilities)
        connection.connect("test-watch-1")

        val result = connection.getCapabilities()

        assertEquals(capabilities, result)
    }

    @Test
    fun getBatteryLevel_returnsValueWhenConnected() = runTest {
        connection.addDevice(testDevice)
        connection.connect("test-watch-1")
        connection.batteryLevel = 75

        assertEquals(75, connection.getBatteryLevel())
    }

    @Test
    fun getBatteryLevel_returnsNullWhenDisconnected() = runTest {
        connection.batteryLevel = 75

        assertNull(connection.getBatteryLevel())
    }

    @Test
    fun simulateConnectionState_updatesState() = runTest {
        connection.simulateConnectionState(ConnectionState.Connecting)

        val state = connection.connectionState.first()
        assertEquals(ConnectionState.Connecting, state)
    }

    @Test
    fun clear_resetsAllState() = runTest {
        connection.addDevice(testDevice)
        connection.connect("test-watch-1")

        connection.clear()

        assertEquals(ConnectionState.Disconnected, connection.connectionState.first())
        assertTrue(connection.discoverDevices().isEmpty())
    }
}
