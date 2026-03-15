package com.moondeveloper.ui

import androidx.compose.ui.graphics.Color

/**
 * Color token interface — each app implements with its own palette.
 * Provide separate instances for light/dark.
 */
interface MoonColorTokens {
    val primary: Color
    val onPrimary: Color
    val primaryContainer: Color
    val onPrimaryContainer: Color
    val secondary: Color
    val onSecondary: Color
    val tertiary: Color
    val onTertiary: Color
    val background: Color
    val onBackground: Color
    val surface: Color
    val onSurface: Color
    val surfaceVariant: Color
    val onSurfaceVariant: Color
    val outline: Color
    val error: Color
    val onError: Color
}

/**
 * Typography size tokens — apps can override or use defaults.
 * Font family is handled separately via Typography override in [MoonTheme].
 */
interface MoonTypographyTokens {
    val displayLargeSp: Int get() = 57
    val displayMediumSp: Int get() = 45
    val displaySmallSp: Int get() = 36
    val headlineLargeSp: Int get() = 32
    val headlineMediumSp: Int get() = 28
    val headlineSmallSp: Int get() = 24
    val titleLargeSp: Int get() = 22
    val titleMediumSp: Int get() = 16
    val titleSmallSp: Int get() = 14
    val bodyLargeSp: Int get() = 16
    val bodyMediumSp: Int get() = 14
    val bodySmallSp: Int get() = 12
    val labelLargeSp: Int get() = 14
    val labelMediumSp: Int get() = 12
    val labelSmallSp: Int get() = 11
}

/** Default implementation when no custom sizes are needed */
object DefaultMoonTypographyTokens : MoonTypographyTokens

/** Spacing tokens — 8dp grid system, shared across all apps */
object MoonSpacing {
    const val XXS = 2
    const val XS = 4
    const val SM = 8
    const val MD = 16
    const val LG = 24
    const val XL = 32
    const val XXL = 48
    const val XXXL = 64
}

/** Shape corner radius tokens — shared across all apps */
object MoonShape {
    const val NONE = 0
    const val XS = 4
    const val SM = 8
    const val MD = 12
    const val LG = 16
    const val XL = 28
    const val FULL = 50
}

/**
 * Per-app theme configuration. Pass to [MoonTheme].
 */
data class MoonThemeConfig(
    val lightColors: MoonColorTokens,
    val darkColors: MoonColorTokens,
    val typography: MoonTypographyTokens = DefaultMoonTypographyTokens,
)
