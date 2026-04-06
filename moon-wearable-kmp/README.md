# moon-wearable-kmp

Platform-agnostic wearable device connectivity for Kotlin Multiplatform.

## Features

- **Device discovery & connection** for smartwatches and fitness trackers
- **Unified messaging** between phone and wearable
- **Data synchronization** with automatic conflict resolution
- **Connection state management** via Flow
- **NoOp & Fake implementations** for testing

## Installation

```kotlin
// includeBuild (local development)
implementation("com.moondeveloper:moon-wearable-kmp")

// Maven Central (coming soon)
implementation("com.moondeveloper:moon-wearable-kmp:1.0.0")
```

## Quick Start

```kotlin
// Discover and connect to a wearable
val connection = FakeWearableConnection()

val devices = connection.discoverDevices()
if (devices.isNotEmpty()) {
    val result = connection.connect(devices.first().id)
    if (result is ConnectionResult.Success) {
        println("Connected to ${result.device.name}")
    }
}

// Observe connection state
connection.connectionState.collect { state ->
    when (state) {
        is ConnectionState.Connected -> println("Connected: ${state.device.name}")
        is ConnectionState.Disconnected -> println("Disconnected")
        is ConnectionState.Connecting -> println("Connecting...")
        is ConnectionState.Error -> println("Error: ${state.reason}")
    }
}

// Send messages to wearable
val messageClient = FakeMessageClient()
messageClient.sendTextMessage("/notification/show", "Hello from phone!")

// Observe incoming messages
messageClient.observeMessages("/command").collect { message ->
    println("Received: ${message.dataAsText()}")
}

// Sync data between devices
val dataSync = FakeDataSync()
dataSync.putStringItem("/settings/theme", "dark")

val theme = dataSync.getDataItem("/settings/theme")?.dataAsString()
```

## API Overview

### Device Types

| Type | Description |
|------|-------------|
| `WearableDevice` | Represents a wearable device |
| `WearableType` | WATCH, FITNESS_BAND, RING, EARBUDS, GLASSES |
| `WearableCapability` | Device capabilities (HEART_RATE, STEPS, GPS, etc.) |

### Connection Management

| Type | Description |
|------|-------------|
| `WearableConnection` | Interface for device discovery and connection |
| `ConnectionState` | Disconnected, Connecting, Connected, Error |
| `ConnectionError` | DEVICE_NOT_FOUND, BLUETOOTH_UNAVAILABLE, etc. |
| `ConnectionResult` | Success or Failure |

### Messaging

| Type | Description |
|------|-------------|
| `MessageClient` | Interface for phone-wearable messaging |
| `WearableMessage` | Received message with path, data, source |
| `MessageResult` | Success or Failure |
| `MessageError` | NOT_CONNECTED, TIMEOUT, etc. |

### Data Synchronization

| Type | Description |
|------|-------------|
| `DataSync` | Interface for persistent data sync |
| `DataItem` | Synchronized data item with path and payload |
| `DataSyncResult` | Success or Failure |
| `DataSyncError` | NOT_CONNECTED, PAYLOAD_TOO_LARGE, etc. |

### Test Implementations

| Type | Description |
|------|-------------|
| `NoOpWearableConnection` | No-op connection for unsupported platforms |
| `NoOpMessageClient` | No-op messaging for unsupported platforms |
| `NoOpDataSync` | No-op data sync for unsupported platforms |
| `FakeWearableConnection` | Fake connection for unit testing |
| `FakeMessageClient` | Fake messaging for unit testing |
| `FakeDataSync` | Fake data sync for unit testing |

## Wearable Capabilities

| Capability | Description |
|------------|-------------|
| RECEIVE_MESSAGES | Can receive messages from phone |
| SEND_MESSAGES | Can send messages to phone |
| DATA_SYNC | Supports Data Layer sync |
| HEART_RATE | Has heart rate sensor |
| STEPS | Has step counter |
| GPS | Has GPS |
| ACCELEROMETER | Has accelerometer |
| GYROSCOPE | Has gyroscope |
| VOICE_CALLS | Can make voice calls |
| APP_INSTALL | Supports app installation |
| TILES | Supports tiles/complications |
| ALWAYS_ON_DISPLAY | Has always-on display |
| BLOOD_OXYGEN | Has SpO2 sensor |
| ECG | Has ECG sensor |
| TEMPERATURE | Has temperature sensor |

## Platform Support

| Platform | Status | Backend |
|----------|--------|---------|
| Android | Supported | Wear Data Layer API |
| iOS | Supported | WatchConnectivity |
| Desktop (JVM) | Supported | NoOp |

## Usage Patterns

### Phone to Watch Communication

```kotlin
// Phone side: send command to watch
messageClient.sendTextMessage("/command/start-workout", "running")

// Watch side: observe commands
messageClient.observeMessages("/command").collect { message ->
    when {
        message.path == "/command/start-workout" -> {
            val type = message.dataAsText()
            startWorkout(type)
        }
    }
}
```

### Settings Synchronization

```kotlin
// Sync user preferences
dataSync.putStringItem("/settings/units", "metric")
dataSync.putStringItem("/settings/notifications", "enabled")

// Watch reads settings
val units = dataSync.getDataItem("/settings/units")?.dataAsString()

// Observe settings changes
dataSync.observeDataItem("/settings/theme").collect { item ->
    item?.let { applyTheme(it.dataAsString()) }
}
```

## License

Apache License 2.0
