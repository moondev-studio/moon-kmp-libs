package com.moondeveloper.wearable

import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

/**
 * Connection state with a wearable device.
 */
sealed class ConnectionState {
    /** Not connected to any wearable */
    data object Disconnected : ConnectionState()

    /** Attempting to connect */
    data object Connecting : ConnectionState()

    /** Connected to a wearable device */
    data class Connected(val device: WearableDevice) : ConnectionState()

    /** Connection failed */
    data class Error(val reason: ConnectionError) : ConnectionState()
}

/**
 * Reasons for connection failure.
 */
enum class ConnectionError {
    /** Device not found */
    DEVICE_NOT_FOUND,

    /** Bluetooth not available */
    BLUETOOTH_UNAVAILABLE,

    /** Bluetooth is off */
    BLUETOOTH_DISABLED,

    /** Companion app not installed on wearable */
    APP_NOT_INSTALLED,

    /** Connection timed out */
    TIMEOUT,

    /** User denied permission */
    PERMISSION_DENIED,

    /** Device is out of range */
    OUT_OF_RANGE,

    /** Already connected to another device */
    ALREADY_CONNECTED,

    /** Unknown error */
    UNKNOWN
}

/**
 * Interface for managing wearable device connections.
 *
 * Platform implementations should delegate to WearableDataLayerClient on Android
 * and WCSession on iOS.
 *
 * @see NoOpWearableConnection for testing and unsupported platforms
 */
interface WearableConnection {

    /**
     * Observe the current connection state.
     */
    val connectionState: Flow<ConnectionState>

    /**
     * Get the currently connected device, if any.
     */
    suspend fun getConnectedDevice(): WearableDevice?

    /**
     * Check if wearable connectivity is available on this device.
     */
    suspend fun isAvailable(): Boolean

    /**
     * Discover available wearable devices.
     *
     * @param timeout Maximum time to wait for discovery
     * @return List of discovered devices
     */
    suspend fun discoverDevices(timeout: Long = 10_000L): List<WearableDevice>

    /**
     * Connect to a specific wearable device.
     *
     * @param deviceId The device ID to connect to
     * @return Result indicating success or failure
     */
    suspend fun connect(deviceId: String): ConnectionResult

    /**
     * Disconnect from the current device.
     */
    suspend fun disconnect()

    /**
     * Get the capabilities of a connected device.
     *
     * @param deviceId Optional device ID (uses connected device if null)
     * @return Set of supported capabilities
     */
    suspend fun getCapabilities(deviceId: String? = null): Set<WearableCapability>

    /**
     * Check if the companion app is installed on the wearable.
     *
     * @param deviceId Optional device ID (uses connected device if null)
     * @return True if installed, false otherwise
     */
    suspend fun isAppInstalled(deviceId: String? = null): Boolean

    /**
     * Get the battery level of the connected wearable.
     *
     * @return Battery percentage (0-100) or null if unavailable
     */
    suspend fun getBatteryLevel(): Int?

    /**
     * Get the last time data was synced with the wearable.
     */
    suspend fun getLastSyncTime(): Instant?
}

/**
 * Result of a connection attempt.
 */
sealed class ConnectionResult {
    data class Success(val device: WearableDevice) : ConnectionResult()
    data class Failure(val error: ConnectionError) : ConnectionResult()
}
