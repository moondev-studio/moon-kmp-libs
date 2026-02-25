# moon-ui-kmp

Adaptive UI components and design token system for Compose Multiplatform.

## Features

- **Window size classes** (Compact, Medium, Expanded) for responsive layouts
- **Adaptive layouts** (AdaptiveContentWidth, TwoPane, ListDetailLayout)
- **Utility composables** (isTablet, isExpandedWidth, isCompactWidth)
- **Theme token interface** for consistent cross-app design
- **CompositionLocal** for window size propagation

## Installation

```kotlin
// includeBuild (local development)
implementation("com.moondeveloper:moon-ui-kmp")

// Maven Central (coming soon)
implementation("com.moondeveloper:moon-ui-kmp:1.0.0")
```

Requires Compose Multiplatform 1.10.1+.

## Quick Start

```kotlin
// Provide window size class at the root
val sizeClass = calculateWindowSizeClass(windowWidth, windowHeight)
CompositionLocalProvider(LocalWindowSizeClass provides sizeClass) {
    MyApp()
}

// Adapt layout based on screen size
@Composable
fun SettlementScreen() {
    if (isTablet()) {
        ListDetailLayout(
            list = { SettlementList() },
            detail = { SettlementDetail() }
        )
    } else {
        SettlementList()
    }
}

// Constrain content width on large screens
AdaptiveContentWidth(maxWidth = 600.dp) {
    Column { /* content stays centered and readable */ }
}
```

## API Overview

| Type | Description |
|------|-------------|
| `WindowSizeClass` | Data class combining width and height classes |
| `WindowWidthClass` | Enum: Compact (<600dp), Medium (600-840dp), Expanded (>=840dp) |
| `WindowHeightClass` | Enum: Compact (<480dp), Medium (480-900dp), Expanded (>=900dp) |
| `calculateWindowSizeClass()` | Calculate size class from Dp dimensions |
| `LocalWindowSizeClass` | CompositionLocal for window size propagation |
| `AdaptiveContentWidth` | Constrains content to max width, centered |
| `TwoPane` | Side-by-side layout with configurable split ratio |
| `ListDetailLayout` | List-detail variant of TwoPane (40/60 split) |
| `isTablet()` | Returns true if width is not Compact |
| `isExpandedWidth()` | Returns true if width is Expanded |
| `isCompactWidth()` | Returns true if width is Compact |
| `MoonThemeTokens` | Interface for app-specific color tokens |

## Platform Support

| Platform | Status |
|----------|--------|
| Android | Supported |
| iOS | Supported |
| Desktop (JVM) | Supported |

## License

Apache License 2.0
