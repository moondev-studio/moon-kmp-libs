package com.moondeveloper.wearable

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlin.time.Instant

/**
 * No-op [WearableConnection] for testing and unsupported platforms.
 */
object NoOpWearableConnection : WearableConnection {

    override val connectionState: Flow<ConnectionState> =
        flowOf(ConnectionState.Disconnected)

    override suspend fun getConnectedDevice(): WearableDevice? = null

    override suspend fun isAvailable(): Boolean = false

    override suspend fun discoverDevices(timeout: Long): List<WearableDevice> = emptyList()

    override suspend fun connect(deviceId: String): ConnectionResult =
        ConnectionResult.Failure(ConnectionError.DEVICE_NOT_FOUND)

    override suspend fun disconnect() {}

    override suspend fun getCapabilities(deviceId: String?): Set<WearableCapability> = emptySet()

    override suspend fun isAppInstalled(deviceId: String?): Boolean = false

    override suspend fun getBatteryLevel(): Int? = null

    override suspend fun getLastSyncTime(): Instant? = null
}

/**
 * No-op [MessageClient] for testing and unsupported platforms.
 */
object NoOpMessageClient : MessageClient {

    override fun observeMessages(path: String?): Flow<WearableMessage> = emptyFlow()

    override suspend fun sendMessage(
        path: String,
        data: ByteArray,
        targetDeviceId: String?
    ): MessageResult = MessageResult.Failure(MessageError.NOT_CONNECTED)

    override suspend fun isAvailable(): Boolean = false
}

/**
 * No-op [DataSync] for testing and unsupported platforms.
 */
object NoOpDataSync : DataSync {

    override fun observeDataItem(path: String): Flow<DataItem?> = flowOf(null)

    override fun observeDataItems(pathPrefix: String): Flow<List<DataItem>> = flowOf(emptyList())

    override suspend fun getDataItem(path: String): DataItem? = null

    override suspend fun getDataItems(pathPrefix: String): List<DataItem> = emptyList()

    override suspend fun putDataItem(
        path: String,
        data: ByteArray,
        urgent: Boolean
    ): DataSyncResult = DataSyncResult.Failure(DataSyncError.NOT_CONNECTED)

    override suspend fun deleteDataItem(path: String): DataSyncResult =
        DataSyncResult.Failure(DataSyncError.NOT_CONNECTED)

    override suspend fun deleteDataItems(pathPrefix: String): Int = 0

    override suspend fun isAvailable(): Boolean = false
}
