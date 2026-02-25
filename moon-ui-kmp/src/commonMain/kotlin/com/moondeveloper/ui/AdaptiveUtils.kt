package com.moondeveloper.ui

import androidx.compose.runtime.Composable

/** Returns `true` if the current window width is not [WindowWidthClass.Compact] (i.e., tablet or larger). */
@Composable
fun isTablet(): Boolean {
    val sizeClass = LocalWindowSizeClass.current
    return sizeClass.width != WindowWidthClass.Compact
}

/** Returns `true` if the current window width is [WindowWidthClass.Expanded] (>=840dp). */
@Composable
fun isExpandedWidth(): Boolean {
    val sizeClass = LocalWindowSizeClass.current
    return sizeClass.width == WindowWidthClass.Expanded
}

/** Returns `true` if the current window width is [WindowWidthClass.Compact] (<600dp). */
@Composable
fun isCompactWidth(): Boolean {
    val sizeClass = LocalWindowSizeClass.current
    return sizeClass.width == WindowWidthClass.Compact
}
