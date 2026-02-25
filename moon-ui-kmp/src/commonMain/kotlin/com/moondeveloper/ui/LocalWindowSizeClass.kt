package com.moondeveloper.ui

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocal providing the current [WindowSizeClass].
 *
 * Provide at the root of your app:
 * ```kotlin
 * CompositionLocalProvider(LocalWindowSizeClass provides sizeClass) { ... }
 * ```
 *
 * Default: Compact width, Medium height.
 */
val LocalWindowSizeClass = staticCompositionLocalOf {
    WindowSizeClass(WindowWidthClass.Compact, WindowHeightClass.Medium)
}
