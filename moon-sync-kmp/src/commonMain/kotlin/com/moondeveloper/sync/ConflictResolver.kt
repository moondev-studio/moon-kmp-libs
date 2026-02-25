package com.moondeveloper.sync

/**
 * Strategy for resolving sync conflicts between local and remote data.
 *
 * @see ServerWinsConflictResolver for a built-in server-wins strategy
 */
interface ConflictResolver {
    suspend fun resolve(
        local: Map<String, Any?>,
        remote: Map<String, Any?>,
        base: Map<String, Any?>? = null
    ): ConflictResolution
}

/** Result of conflict resolution between local and remote data. */
sealed class ConflictResolution {
    data class UseLocal(val data: Map<String, Any?>) : ConflictResolution()
    data class UseRemote(val data: Map<String, Any?>) : ConflictResolution()
    data class Merged(val data: Map<String, Any?>) : ConflictResolution()
    data class RequireUserInput(
        val local: Map<String, Any?>,
        val remote: Map<String, Any?>
    ) : ConflictResolution()
}
