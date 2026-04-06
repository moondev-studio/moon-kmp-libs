package com.moondeveloper.wearable

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter

/**
 * Fake [MessageClient] for unit testing.
 */
class FakeMessageClient : MessageClient {

    private val messageFlow = MutableSharedFlow<WearableMessage>()

    /** Sent messages for verification */
    val sentMessages = mutableListOf<SentMessage>()

    var isAvailableValue = true
    var simulateError: MessageError? = null

    /** Simulate receiving a message */
    suspend fun receiveMessage(message: WearableMessage) {
        messageFlow.emit(message)
    }

    /** Clear sent messages */
    fun clear() {
        sentMessages.clear()
        simulateError = null
    }

    override fun observeMessages(path: String?): Flow<WearableMessage> {
        return if (path == null) {
            messageFlow
        } else {
            messageFlow.filter { it.path == path || it.path.startsWith("$path/") }
        }
    }

    override suspend fun sendMessage(
        path: String,
        data: ByteArray,
        targetDeviceId: String?
    ): MessageResult {
        simulateError?.let { error ->
            return MessageResult.Failure(error)
        }

        sentMessages.add(SentMessage(path, data.copyOf(), targetDeviceId))
        return MessageResult.Success
    }

    override suspend fun isAvailable(): Boolean = isAvailableValue

    /**
     * Record of a sent message.
     */
    data class SentMessage(
        val path: String,
        val data: ByteArray,
        val targetDeviceId: String?
    ) {
        fun dataAsText(): String = data.decodeToString()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as SentMessage

            if (path != other.path) return false
            if (!data.contentEquals(other.data)) return false
            if (targetDeviceId != other.targetDeviceId) return false

            return true
        }

        override fun hashCode(): Int {
            var result = path.hashCode()
            result = 31 * result + data.contentHashCode()
            result = 31 * result + (targetDeviceId?.hashCode() ?: 0)
            return result
        }
    }
}
