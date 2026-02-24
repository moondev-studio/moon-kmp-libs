package com.moondeveloper.ui

import androidx.compose.ui.graphics.Color

/**
 * Common design token interface for all MoonDeveloper apps.
 * Each app implements this with its own color scheme.
 */
interface MoonThemeTokens {
    val primary: Color
    val onPrimary: Color
    val primaryContainer: Color
    val onPrimaryContainer: Color
    val secondary: Color
    val onSecondary: Color
    val background: Color
    val onBackground: Color
    val surface: Color
    val onSurface: Color
    val error: Color
    val onError: Color
}
