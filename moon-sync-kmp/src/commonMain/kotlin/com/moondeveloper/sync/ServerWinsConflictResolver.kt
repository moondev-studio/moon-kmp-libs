package com.moondeveloper.sync

class ServerWinsConflictResolver : ConflictResolver {
    override suspend fun resolve(
        local: Map<String, Any?>,
        remote: Map<String, Any?>,
        base: Map<String, Any?>?
    ) = ConflictResolution.UseRemote(remote)
}
