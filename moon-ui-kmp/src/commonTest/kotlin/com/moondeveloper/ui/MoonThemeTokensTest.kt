package com.moondeveloper.ui

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class MoonThemeTokensTest {

    private val testLightColors = object : MoonColorTokens {
        override val primary = Color(0xFF6200EE)
        override val onPrimary = Color.White
        override val primaryContainer = Color(0xFFBB86FC)
        override val onPrimaryContainer = Color.Black
        override val secondary = Color(0xFF03DAC6)
        override val onSecondary = Color.Black
        override val tertiary = Color(0xFF018786)
        override val onTertiary = Color.White
        override val background = Color.White
        override val onBackground = Color.Black
        override val surface = Color.White
        override val onSurface = Color.Black
        override val surfaceVariant = Color(0xFFE7E0EC)
        override val onSurfaceVariant = Color(0xFF49454F)
        override val outline = Color(0xFF79747E)
        override val error = Color(0xFFB00020)
        override val onError = Color.White
    }

    private val testDarkColors = object : MoonColorTokens {
        override val primary = Color(0xFFBB86FC)
        override val onPrimary = Color.Black
        override val primaryContainer = Color(0xFF6200EE)
        override val onPrimaryContainer = Color.White
        override val secondary = Color(0xFF03DAC6)
        override val onSecondary = Color.Black
        override val tertiary = Color(0xFF018786)
        override val onTertiary = Color.White
        override val background = Color(0xFF121212)
        override val onBackground = Color.White
        override val surface = Color(0xFF121212)
        override val onSurface = Color.White
        override val surfaceVariant = Color(0xFF49454F)
        override val onSurfaceVariant = Color(0xFFCAC4D0)
        override val outline = Color(0xFF938F99)
        override val error = Color(0xFFCF6679)
        override val onError = Color.Black
    }

    @Test
    fun light_dark_primary_colors_are_different() {
        assertNotEquals(testLightColors.primary, testDarkColors.primary)
    }

    @Test
    fun moon_spacing_values_are_8dp_grid() {
        assertEquals(8, MoonSpacing.SM)
        assertEquals(16, MoonSpacing.MD)
        assertEquals(24, MoonSpacing.LG)
    }

    @Test
    fun moon_spacing_progressive_scale() {
        assert(MoonSpacing.XS < MoonSpacing.SM)
        assert(MoonSpacing.SM < MoonSpacing.MD)
        assert(MoonSpacing.MD < MoonSpacing.LG)
        assert(MoonSpacing.LG < MoonSpacing.XL)
    }

    @Test
    fun default_typography_tokens_have_correct_scale() {
        val typo = DefaultMoonTypographyTokens
        assert(typo.displayLargeSp > typo.headlineLargeSp)
        assert(typo.headlineLargeSp > typo.titleLargeSp)
        assert(typo.titleLargeSp > typo.bodyLargeSp)
        assert(typo.bodyLargeSp > typo.labelSmallSp)
    }

    @Test
    fun moon_theme_config_holds_light_and_dark() {
        val config = MoonThemeConfig(
            lightColors = testLightColors,
            darkColors = testDarkColors,
        )
        assertEquals(testLightColors.primary, config.lightColors.primary)
        assertEquals(testDarkColors.primary, config.darkColors.primary)
    }

    @Test
    fun moon_shape_progressive_scale() {
        assert(MoonShape.NONE < MoonShape.XS)
        assert(MoonShape.XS < MoonShape.SM)
        assert(MoonShape.SM < MoonShape.MD)
        assert(MoonShape.MD < MoonShape.LG)
        assert(MoonShape.LG < MoonShape.XL)
        assert(MoonShape.XL < MoonShape.FULL)
    }

    @Test
    fun moon_theme_config_default_typography() {
        val config = MoonThemeConfig(
            lightColors = testLightColors,
            darkColors = testDarkColors,
        )
        assertEquals(DefaultMoonTypographyTokens, config.typography)
    }
}
