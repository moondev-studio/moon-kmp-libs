# moon-analytics-kmp

Platform-agnostic analytics, crash reporting, and event tracking for Kotlin Multiplatform.

## Features

- **Event tracking** with categories and parameters
- **Crash reporting** with contextual metadata
- **Screen tracking** for navigation analytics
- **User action tracking** (button clicks, form submits, dialog responses)
- **Performance tracing** with configurable sampling
- **Conversion tracking** (paywall, purchase funnel)
- **Composite pattern** to fan-out events to multiple backends
- **NoOp implementations** for testing and unsupported platforms

## Installation

```kotlin
// includeBuild (local development)
implementation("com.moondeveloper:moon-analytics-kmp")

// Maven Central (coming soon)
implementation("com.moondeveloper:moon-analytics-kmp:1.0.0")
```

## Quick Start

```kotlin
// Use NoOp implementations for testing
val analytics = CompositeAnalytics(
    tracker = NoOpTracker,
    crashReporter = NoOpCrashReporter,
    screenTracker = NoOpScreenTracker,
    actionTracker = NoOpUserActionTracker,
    performanceTracker = NoOpPerformanceTracker,
    conversionTracker = NoOpConversionTracker,
    performanceSampleRate = 0.1f
)

// Track events
analytics.logEvent(AnalyticsEvent("button_click", EventCategory.USER_ACTION))
analytics.trackScreenView("HomeScreen")
analytics.trackButtonClick("save_button", "SettingsScreen")

// Performance tracing (sampled)
val trace = analytics.startTrace("api_call")
trace.putAttribute("endpoint", "/users")
// ... do work ...
trace.stop()
```

## API Overview

| Type | Description |
|------|-------------|
| `AnalyticsTracker` | Core event tracking interface |
| `CrashReporter` | Exception and log recording |
| `ScreenTracker` | Screen view tracking |
| `UserActionTracker` | Button clicks, form submits, dialog responses |
| `PerformanceTracker` | Trace-based performance measurement |
| `ConversionTracker` | Paywall and purchase funnel tracking |
| `CompositeAnalytics` | Unified facade delegating to all trackers |
| `CompositeTracker` | Fan-out to multiple `AnalyticsTracker` instances |
| `CompositeCrashReporter` | Fan-out to multiple `CrashReporter` instances |
| `AnalyticsEvent` | Event data class with name, category, params |
| `EventCategory` | Enum: SCREEN_VIEW, USER_ACTION, FEATURE_USAGE, ERROR, PERFORMANCE, CONVERSION, RETENTION |
| `AnalyticsSampling` | Sampling logic (performance events are probabilistic) |
| `TraceHandle` | Handle for in-progress performance traces |
| `NoOpTracker` | No-op `AnalyticsTracker` |
| `NoOpCrashReporter` | No-op `CrashReporter` |
| `NoOpScreenTracker` | No-op `ScreenTracker` |
| `NoOpUserActionTracker` | No-op `UserActionTracker` |
| `NoOpPerformanceTracker` | No-op `PerformanceTracker` |
| `NoOpConversionTracker` | No-op `ConversionTracker` |

## Platform Support

| Platform | Status |
|----------|--------|
| Android | Supported |
| iOS | Supported |
| Desktop (JVM) | Supported |

## License

Apache License 2.0
