package com.moondeveloper.wearable

import kotlinx.coroutines.flow.Flow

/**
 * Interface for synchronizing data between phone and wearable.
 *
 * Data items are persistent, automatically synchronized, and survive app restarts.
 * Use for settings, preferences, and small datasets.
 *
 * @see NoOpDataSync for testing and unsupported platforms
 */
interface DataSync {

    /**
     * Observe changes to a data item.
     *
     * @param path Data item path (e.g., "/settings/notifications")
     * @return Flow emitting data changes
     */
    fun observeDataItem(path: String): Flow<DataItem?>

    /**
     * Observe all data items under a path prefix.
     *
     * @param pathPrefix Path prefix to watch (e.g., "/settings")
     * @return Flow emitting lists of data items
     */
    fun observeDataItems(pathPrefix: String): Flow<List<DataItem>>

    /**
     * Get a data item by path.
     *
     * @param path Data item path
     * @return The data item, or null if not found
     */
    suspend fun getDataItem(path: String): DataItem?

    /**
     * Get all data items under a path prefix.
     *
     * @param pathPrefix Path prefix to query
     * @return List of matching data items
     */
    suspend fun getDataItems(pathPrefix: String): List<DataItem>

    /**
     * Put a data item to be synced.
     *
     * @param path Data item path
     * @param data Payload data
     * @param urgent If true, sync immediately (uses more battery)
     * @return Result indicating success or failure
     */
    suspend fun putDataItem(
        path: String,
        data: ByteArray,
        urgent: Boolean = false
    ): DataSyncResult

    /**
     * Put a string data item (convenience method).
     */
    suspend fun putStringItem(
        path: String,
        value: String,
        urgent: Boolean = false
    ): DataSyncResult = putDataItem(path, value.encodeToByteArray(), urgent)

    /**
     * Delete a data item.
     *
     * @param path Data item path to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteDataItem(path: String): DataSyncResult

    /**
     * Delete all data items under a path prefix.
     *
     * @param pathPrefix Path prefix to delete
     * @return Number of items deleted
     */
    suspend fun deleteDataItems(pathPrefix: String): Int

    /**
     * Check if data sync is available.
     */
    suspend fun isAvailable(): Boolean
}

/**
 * A synchronized data item.
 */
data class DataItem(
    /** Full path of the data item */
    val path: String,

    /** Payload data */
    val data: ByteArray,

    /** Timestamp of last modification (epoch millis) */
    val lastModified: Long
) {
    /** Decode payload as UTF-8 text */
    fun dataAsString(): String = data.decodeToString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DataItem

        if (path != other.path) return false
        if (!data.contentEquals(other.data)) return false
        if (lastModified != other.lastModified) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + data.contentHashCode()
        result = 31 * result + lastModified.hashCode()
        return result
    }
}

/**
 * Result of a data sync operation.
 */
sealed class DataSyncResult {
    data object Success : DataSyncResult()
    data class Failure(val error: DataSyncError) : DataSyncResult()
}

/**
 * Errors that can occur during data sync.
 */
enum class DataSyncError {
    /** No device connected */
    NOT_CONNECTED,

    /** Data too large */
    PAYLOAD_TOO_LARGE,

    /** Path is invalid */
    INVALID_PATH,

    /** Sync service unavailable */
    SERVICE_UNAVAILABLE,

    /** Unknown error */
    UNKNOWN
}
