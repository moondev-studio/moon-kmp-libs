package com.moondeveloper.wearable

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Fake [WearableConnection] for unit testing.
 */
class FakeWearableConnection : WearableConnection {

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: Flow<ConnectionState> = _connectionState.asStateFlow()

    private var connectedDevice: WearableDevice? = null
    private val devices = mutableListOf<WearableDevice>()
    private val capabilities = mutableMapOf<String, Set<WearableCapability>>()

    var isAvailableValue = true
    var batteryLevel: Int? = 80
    var lastSyncTime: Instant? = Clock.System.now()

    /** Add a discoverable device */
    fun addDevice(device: WearableDevice, deviceCapabilities: Set<WearableCapability> = emptySet()) {
        devices.add(device)
        capabilities[device.id] = deviceCapabilities
    }

    /** Simulate connection state change */
    fun simulateConnectionState(state: ConnectionState) {
        _connectionState.value = state
        connectedDevice = when (state) {
            is ConnectionState.Connected -> state.device
            else -> null
        }
    }

    /** Clear all state */
    fun clear() {
        _connectionState.value = ConnectionState.Disconnected
        connectedDevice = null
        devices.clear()
        capabilities.clear()
    }

    override suspend fun getConnectedDevice(): WearableDevice? = connectedDevice

    override suspend fun isAvailable(): Boolean = isAvailableValue

    override suspend fun discoverDevices(timeout: Long): List<WearableDevice> = devices.toList()

    override suspend fun connect(deviceId: String): ConnectionResult {
        val device = devices.find { it.id == deviceId }
            ?: return ConnectionResult.Failure(ConnectionError.DEVICE_NOT_FOUND)

        connectedDevice = device
        _connectionState.value = ConnectionState.Connected(device)
        return ConnectionResult.Success(device)
    }

    override suspend fun disconnect() {
        connectedDevice = null
        _connectionState.value = ConnectionState.Disconnected
    }

    override suspend fun getCapabilities(deviceId: String?): Set<WearableCapability> {
        val id = deviceId ?: connectedDevice?.id ?: return emptySet()
        return capabilities[id] ?: emptySet()
    }

    override suspend fun isAppInstalled(deviceId: String?): Boolean = connectedDevice != null

    override suspend fun getBatteryLevel(): Int? = if (connectedDevice != null) batteryLevel else null

    override suspend fun getLastSyncTime(): Instant? = if (connectedDevice != null) lastSyncTime else null
}
