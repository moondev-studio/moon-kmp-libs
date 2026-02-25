package com.moondeveloper.sync

/** [ConflictResolver] that always resolves in favor of the remote (server) data. */
class ServerWinsConflictResolver : ConflictResolver {
    override suspend fun resolve(
        local: Map<String, Any?>,
        remote: Map<String, Any?>,
        base: Map<String, Any?>?
    ) = ConflictResolution.UseRemote(remote)
}
