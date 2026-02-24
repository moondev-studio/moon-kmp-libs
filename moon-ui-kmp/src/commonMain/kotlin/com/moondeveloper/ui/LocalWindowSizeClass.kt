package com.moondeveloper.ui

import androidx.compose.runtime.staticCompositionLocalOf

val LocalWindowSizeClass = staticCompositionLocalOf {
    WindowSizeClass(WindowWidthClass.Compact, WindowHeightClass.Medium)
}
