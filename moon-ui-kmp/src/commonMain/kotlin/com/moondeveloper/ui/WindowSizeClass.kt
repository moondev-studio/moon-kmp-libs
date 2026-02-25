package com.moondeveloper.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Width-based breakpoint classification for responsive layouts. */
enum class WindowWidthClass {
    /** Phone portrait: width < 600dp */
    Compact,
    /** Phone landscape / small tablet: 600dp <= width < 840dp */
    Medium,
    /** Tablet / desktop: width >= 840dp */
    Expanded
}

/** Height-based breakpoint classification for responsive layouts. */
enum class WindowHeightClass {
    /** height < 480dp */
    Compact,
    /** 480dp <= height < 900dp */
    Medium,
    /** height >= 900dp */
    Expanded
}

/** Combined width and height size classes for the current window. */
data class WindowSizeClass(
    val width: WindowWidthClass,
    val height: WindowHeightClass
)

/** Calculate the [WindowSizeClass] from the given window dimensions. */
fun calculateWindowSizeClass(width: Dp, height: Dp): WindowSizeClass {
    val widthClass = when {
        width < 600.dp -> WindowWidthClass.Compact
        width < 840.dp -> WindowWidthClass.Medium
        else -> WindowWidthClass.Expanded
    }
    val heightClass = when {
        height < 480.dp -> WindowHeightClass.Compact
        height < 900.dp -> WindowHeightClass.Medium
        else -> WindowHeightClass.Expanded
    }
    return WindowSizeClass(widthClass, heightClass)
}
