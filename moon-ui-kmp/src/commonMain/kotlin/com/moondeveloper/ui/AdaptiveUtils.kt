package com.moondeveloper.ui

import androidx.compose.runtime.Composable

@Composable
fun isTablet(): Boolean {
    val sizeClass = LocalWindowSizeClass.current
    return sizeClass.width != WindowWidthClass.Compact
}

@Composable
fun isExpandedWidth(): Boolean {
    val sizeClass = LocalWindowSizeClass.current
    return sizeClass.width == WindowWidthClass.Expanded
}

@Composable
fun isCompactWidth(): Boolean {
    val sizeClass = LocalWindowSizeClass.current
    return sizeClass.width == WindowWidthClass.Compact
}
