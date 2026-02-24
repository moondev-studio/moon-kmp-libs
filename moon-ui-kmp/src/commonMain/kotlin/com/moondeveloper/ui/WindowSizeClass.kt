package com.moondeveloper.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowWidthClass {
    /** Phone portrait: width < 600dp */
    Compact,
    /** Phone landscape / small tablet: 600dp <= width < 840dp */
    Medium,
    /** Tablet / desktop: width >= 840dp */
    Expanded
}

enum class WindowHeightClass {
    /** height < 480dp */
    Compact,
    /** 480dp <= height < 900dp */
    Medium,
    /** height >= 900dp */
    Expanded
}

data class WindowSizeClass(
    val width: WindowWidthClass,
    val height: WindowHeightClass
)

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
