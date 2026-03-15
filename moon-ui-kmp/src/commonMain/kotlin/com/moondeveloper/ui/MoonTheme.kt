package com.moondeveloper.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

/** Current app's color tokens */
val LocalMoonColors = staticCompositionLocalOf<MoonColorTokens> {
    error("MoonTheme not provided")
}

/** Current app's typography tokens */
val LocalMoonTypography = staticCompositionLocalOf<MoonTypographyTokens> {
    DefaultMoonTypographyTokens
}

/**
 * Common theme entry point for all MoonDeveloper apps.
 *
 * @param config App-specific color and typography tokens.
 * @param darkTheme Whether to use dark mode colors.
 * @param typography Optional full Typography override (e.g., for custom fonts).
 *   When null, typography is generated from [MoonThemeConfig.typography] tokens.
 * @param content Composable content.
 */
@Composable
fun MoonTheme(
    config: MoonThemeConfig,
    darkTheme: Boolean = isSystemInDarkTheme(),
    typography: Typography? = null,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) config.darkColors else config.lightColors

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = colors.primary,
            onPrimary = colors.onPrimary,
            primaryContainer = colors.primaryContainer,
            onPrimaryContainer = colors.onPrimaryContainer,
            secondary = colors.secondary,
            onSecondary = colors.onSecondary,
            tertiary = colors.tertiary,
            onTertiary = colors.onTertiary,
            background = colors.background,
            onBackground = colors.onBackground,
            surface = colors.surface,
            onSurface = colors.onSurface,
            surfaceVariant = colors.surfaceVariant,
            onSurfaceVariant = colors.onSurfaceVariant,
            outline = colors.outline,
            error = colors.error,
            onError = colors.onError,
        )
    } else {
        lightColorScheme(
            primary = colors.primary,
            onPrimary = colors.onPrimary,
            primaryContainer = colors.primaryContainer,
            onPrimaryContainer = colors.onPrimaryContainer,
            secondary = colors.secondary,
            onSecondary = colors.onSecondary,
            tertiary = colors.tertiary,
            onTertiary = colors.onTertiary,
            background = colors.background,
            onBackground = colors.onBackground,
            surface = colors.surface,
            onSurface = colors.onSurface,
            surfaceVariant = colors.surfaceVariant,
            onSurfaceVariant = colors.onSurfaceVariant,
            outline = colors.outline,
            error = colors.error,
            onError = colors.onError,
        )
    }

    val typo = config.typography
    val resolvedTypography = typography ?: Typography(
        displayLarge = TextStyle(fontSize = typo.displayLargeSp.sp),
        displayMedium = TextStyle(fontSize = typo.displayMediumSp.sp),
        displaySmall = TextStyle(fontSize = typo.displaySmallSp.sp),
        headlineLarge = TextStyle(fontSize = typo.headlineLargeSp.sp),
        headlineMedium = TextStyle(fontSize = typo.headlineMediumSp.sp),
        headlineSmall = TextStyle(fontSize = typo.headlineSmallSp.sp),
        titleLarge = TextStyle(fontSize = typo.titleLargeSp.sp),
        titleMedium = TextStyle(fontSize = typo.titleMediumSp.sp),
        titleSmall = TextStyle(fontSize = typo.titleSmallSp.sp),
        bodyLarge = TextStyle(fontSize = typo.bodyLargeSp.sp),
        bodyMedium = TextStyle(fontSize = typo.bodyMediumSp.sp),
        bodySmall = TextStyle(fontSize = typo.bodySmallSp.sp),
        labelLarge = TextStyle(fontSize = typo.labelLargeSp.sp),
        labelMedium = TextStyle(fontSize = typo.labelMediumSp.sp),
        labelSmall = TextStyle(fontSize = typo.labelSmallSp.sp),
    )

    CompositionLocalProvider(
        LocalMoonColors provides colors,
        LocalMoonTypography provides typo,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = resolvedTypography,
            content = content,
        )
    }
}
