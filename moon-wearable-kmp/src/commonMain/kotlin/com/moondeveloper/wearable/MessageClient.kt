package com.moondeveloper.wearable

import kotlinx.coroutines.flow.Flow

/**
 * Interface for sending and receiving messages between phone and wearable.
 *
 * Messages are lightweight, fire-and-forget communications suitable for
 * commands, notifications, and small data payloads.
 *
 * @see NoOpMessageClient for testing and unsupported platforms
 */
interface MessageClient {

    /**
     * Observe incoming messages on a specific path.
     *
     * @param path Message path to observe (e.g., "/command/start")
     * @return Flow of received messages
     */
    fun observeMessages(path: String? = null): Flow<WearableMessage>

    /**
     * Send a message to the connected wearable.
     *
     * @param path Message path identifying the message type
     * @param data Optional payload data
     * @param targetDeviceId Specific device ID (null for all connected devices)
     * @return Result indicating success or failure
     */
    suspend fun sendMessage(
        path: String,
        data: ByteArray = ByteArray(0),
        targetDeviceId: String? = null
    ): MessageResult

    /**
     * Send a text message (convenience method).
     */
    suspend fun sendTextMessage(
        path: String,
        text: String,
        targetDeviceId: String? = null
    ): MessageResult = sendMessage(path, text.encodeToByteArray(), targetDeviceId)

    /**
     * Check if messaging is available.
     */
    suspend fun isAvailable(): Boolean
}

/**
 * A message received from a wearable device.
 */
data class WearableMessage(
    /** Message path (e.g., "/sync/complete") */
    val path: String,

    /** Message payload */
    val data: ByteArray,

    /** ID of the device that sent the message */
    val sourceDeviceId: String
) {
    /** Decode payload as UTF-8 text */
    fun dataAsText(): String = data.decodeToString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as WearableMessage

        if (path != other.path) return false
        if (!data.contentEquals(other.data)) return false
        if (sourceDeviceId != other.sourceDeviceId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + data.contentHashCode()
        result = 31 * result + sourceDeviceId.hashCode()
        return result
    }
}

/**
 * Result of a message send operation.
 */
sealed class MessageResult {
    data object Success : MessageResult()
    data class Failure(val error: MessageError) : MessageResult()
}

/**
 * Errors that can occur when sending messages.
 */
enum class MessageError {
    /** No device connected */
    NOT_CONNECTED,

    /** Target device not found */
    DEVICE_NOT_FOUND,

    /** Message too large */
    PAYLOAD_TOO_LARGE,

    /** Send timed out */
    TIMEOUT,

    /** Unknown error */
    UNKNOWN
}
