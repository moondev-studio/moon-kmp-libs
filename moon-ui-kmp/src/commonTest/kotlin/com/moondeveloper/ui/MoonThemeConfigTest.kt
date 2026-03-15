package com.moondeveloper.ui

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class MoonThemeConfigTest {

    private fun makeTokens(primary: Color, background: Color = Color.White) = object : MoonColorTokens {
        override val primary = primary
        override val onPrimary = Color.White
        override val primaryContainer = Color.White
        override val onPrimaryContainer = Color.Black
        override val secondary = Color.Gray
        override val onSecondary = Color.Black
        override val tertiary = Color.Gray
        override val onTertiary = Color.Black
        override val background = background
        override val onBackground = Color.Black
        override val surface = Color.White
        override val onSurface = Color.Black
        override val surfaceVariant = Color.LightGray
        override val onSurfaceVariant = Color.DarkGray
        override val outline = Color.Gray
        override val error = Color.Red
        override val onError = Color.White
    }

    @Test
    fun config_uses_default_typography_when_not_specified() {
        val config = MoonThemeConfig(
            lightColors = makeTokens(Color.Blue),
            darkColors = makeTokens(Color.Cyan),
        )
        assertEquals(DefaultMoonTypographyTokens, config.typography)
    }

    @Test
    fun config_uses_custom_typography_when_provided() {
        val customTypo = object : MoonTypographyTokens {
            override val bodyLargeSp: Int get() = 20
        }
        val config = MoonThemeConfig(
            lightColors = makeTokens(Color.Blue),
            darkColors = makeTokens(Color.Cyan),
            typography = customTypo,
        )
        assertEquals(20, config.typography.bodyLargeSp)
    }

    @Test
    fun config_light_and_dark_colors_are_independent() {
        val light = makeTokens(Color(0xFF6200EE), Color.White)
        val dark = makeTokens(Color(0xFFBB86FC), Color(0xFF121212))
        val config = MoonThemeConfig(lightColors = light, darkColors = dark)

        assertNotEquals(config.lightColors.primary, config.darkColors.primary)
        assertNotEquals(config.lightColors.background, config.darkColors.background)
    }

    @Test
    fun data_class_copy_preserves_colors() {
        val config = MoonThemeConfig(
            lightColors = makeTokens(Color.Blue),
            darkColors = makeTokens(Color.Cyan),
        )
        val customTypo = object : MoonTypographyTokens {
            override val displayLargeSp: Int get() = 60
        }
        val updated = config.copy(typography = customTypo)

        assertEquals(config.lightColors, updated.lightColors)
        assertEquals(config.darkColors, updated.darkColors)
        assertEquals(60, updated.typography.displayLargeSp)
    }
}
