# moon-health-kmp

Platform-agnostic health data abstraction for Kotlin Multiplatform.

## Features

- **Health data types** for heart rate, steps, sleep, workouts, and more
- **Unified API** abstracting HealthConnect (Android) and HealthKit (iOS)
- **Permission handling** with consistent cross-platform status
- **Data aggregation** (sum, average, min, max, count)
- **Change observation** via Flow
- **Sleep & Workout** detailed session data classes
- **NoOp & Fake implementations** for testing

## Installation

```kotlin
// includeBuild (local development)
implementation("com.moondeveloper:moon-health-kmp")

// Maven Central (coming soon)
implementation("com.moondeveloper:moon-health-kmp:1.0.0")
```

## Quick Start

```kotlin
// Use FakeHealthDataProvider for testing
val provider = FakeHealthDataProvider()

// Check permissions
val permissions = provider.checkPermissions(setOf(HealthDataType.STEPS, HealthDataType.HEART_RATE))

// Request permissions
val result = provider.requestPermissions(setOf(HealthDataType.STEPS))

// Read records
val now = Clock.System.now()
val records = provider.readRecords(
    type = HealthDataType.STEPS,
    startTime = now.minus(24.hours),
    endTime = now
)

// Aggregate data
val totalSteps = provider.aggregate(
    type = HealthDataType.STEPS,
    startTime = now.minus(24.hours),
    endTime = now,
    aggregationType = AggregationType.SUM
)

// Observe changes
provider.observeChanges(setOf(HealthDataType.STEPS))
    .collect { change ->
        println("Data changed: ${change.type}, ${change.changeType}")
    }
```

## API Overview

### Core Types

| Type | Description |
|------|-------------|
| `HealthDataType` | Enum of supported health data types |
| `HealthRecord` | Single health data record with time, value, unit |
| `HealthUnit` | Units of measurement (BPM, COUNT, KCAL, etc.) |
| `DataSource` | Information about the data source (app, device) |
| `DeviceType` | Type of device (PHONE, WATCH, SCALE, etc.) |

### Provider Interface

| Type | Description |
|------|-------------|
| `HealthDataProvider` | Main interface for reading/writing health data |
| `NoOpHealthDataProvider` | No-op implementation for unsupported platforms |
| `FakeHealthDataProvider` | Fake implementation for unit testing |

### Result Types

| Type | Description |
|------|-------------|
| `PermissionStatus` | GRANTED, DENIED, NOT_DETERMINED, RESTRICTED |
| `PermissionResult` | Success or Denied with reason |
| `ReadResult` | Success with records or Error |
| `WriteResult` | Success with ID or Error |
| `DeleteResult` | Success or Error |
| `AggregateResult` | Success with value or Error |
| `ErrorCode` | PERMISSION_DENIED, UNSUPPORTED_TYPE, NOT_FOUND, etc. |

### Session Data

| Type | Description |
|------|-------------|
| `SleepSession` | Detailed sleep data with stages |
| `SleepStage` | Individual sleep stage (light, deep, REM) |
| `SleepStageType` | AWAKE, LIGHT, DEEP, REM, etc. |
| `WorkoutSession` | Detailed workout data with metrics |
| `WorkoutSegment` | Segment within a workout (lap, interval) |
| `WorkoutType` | RUNNING, CYCLING, YOGA, etc. |

## Supported Health Data Types

| Type | Description |
|------|-------------|
| HEART_RATE | Heart rate (BPM) |
| STEPS | Step count |
| DISTANCE | Distance traveled (meters) |
| ACTIVE_CALORIES | Active calories burned |
| TOTAL_CALORIES | Total calories (active + basal) |
| BLOOD_OXYGEN | SpO2 percentage |
| SLEEP | Sleep session |
| WORKOUT | Workout session |
| WEIGHT | Body weight (kg) |
| HEIGHT | Body height (cm) |
| BLOOD_PRESSURE | Blood pressure (mmHg) |
| BODY_TEMPERATURE | Temperature (Celsius) |
| RESPIRATORY_RATE | Breaths per minute |
| RESTING_HEART_RATE | Resting heart rate |
| HEART_RATE_VARIABILITY | HRV (SDNN in ms) |
| FLOORS_CLIMBED | Floors climbed |
| HYDRATION | Water intake (ml) |
| NUTRITION | Nutrition data |

## Platform Support

| Platform | Status | Backend |
|----------|--------|---------|
| Android | Supported | HealthConnect |
| iOS | Supported | HealthKit |
| Desktop (JVM) | Supported | NoOp |

## License

Apache License 2.0
