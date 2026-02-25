# moon-sync-kmp

Offline-first sync engine with conflict resolution for Kotlin Multiplatform.

## Features

- **Queue-based sync** with automatic retry (configurable max retries)
- **Conflict resolution** strategies (server-wins built-in, custom via interface)
- **Network monitoring** abstraction with reactive `StateFlow<Boolean>`
- **Remote store** abstraction (Firestore, REST, or any backend)
- **Sync state observation** via `StateFlow<SyncState>`
- **NoOp implementations** for testing and offline-only scenarios

## Installation

```kotlin
// includeBuild (local development)
implementation("com.moondeveloper:moon-sync-kmp")

// Maven Central (coming soon)
implementation("com.moondeveloper:moon-sync-kmp:1.0.0")
```

## Quick Start

```kotlin
val syncManager: SyncManager = DefaultSyncManager(
    remoteStore = myFirestoreStore,
    conflictResolver = ServerWinsConflictResolver(),
    networkMonitor = myNetworkMonitor,
    queueStore = myQueueStore,
    maxRetries = 3
)

// Enqueue changes (auto-syncs when online)
syncManager.enqueue(
    entityType = "settlements",
    entityId = "abc123",
    action = SyncAction.CREATE,
    payload = """{"title":"Dinner","amount":50000}"""
)

// Observe sync state
syncManager.syncState.collect { state ->
    when (state) {
        is SyncState.Idle -> hideProgress()
        is SyncState.Syncing -> showProgress()
        is SyncState.Completed -> showSuccess("${state.count} synced")
        is SyncState.Error -> showError(state.message)
    }
}

// Observe pending count
syncManager.pendingCount.collect { count ->
    updateBadge(count)
}
```

## API Overview

| Type | Description |
|------|-------------|
| `SyncManager` | Core sync interface (enqueue, process, clear) |
| `DefaultSyncManager` | Default implementation with retry and network awareness |
| `RemoteStore` | Remote data store abstraction (CRUD + query) |
| `ConflictResolver` | Conflict resolution strategy interface |
| `ServerWinsConflictResolver` | Built-in server-wins strategy |
| `NetworkMonitor` | Network connectivity monitoring |
| `SyncQueueStore` | Persistent sync queue storage |
| `SyncQueueItem` | Queue item data (entity, action, retry count) |
| `SyncAction` | Enum: CREATE, UPDATE, DELETE |
| `SyncState` | Sealed class: Idle, Syncing, Completed, Error |
| `SyncResult` | Sealed class: Success, Failure, Partial |
| `ConflictResolution` | Sealed class: UseLocal, UseRemote, Merged, RequireUserInput |
| `QueryFilter` | Query filter with field, operator, value |
| `FilterOperator` | Enum: EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN, ARRAY_CONTAINS, IN |
| `NoOpSyncManager` | No-op sync (all operations are no-ops) |
| `NoOpNetworkMonitor` | Always online |

## Platform Support

| Platform | Status |
|----------|--------|
| Android | Supported |
| iOS | Supported |
| Desktop (JVM) | Supported |

## License

Apache License 2.0
