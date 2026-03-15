package com.moondeveloper.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MoonThemeTokensTest {

    @Test
    fun default_typography_body_large_is_16sp() {
        assertEquals(16, DefaultMoonTypographyTokens.bodyLargeSp)
    }

    @Test
    fun default_typography_display_large_is_57sp() {
        assertEquals(57, DefaultMoonTypographyTokens.displayLargeSp)
    }

    @Test
    fun default_typography_label_small_is_11sp() {
        assertEquals(11, DefaultMoonTypographyTokens.labelSmallSp)
    }

    @Test
    fun typography_scale_is_descending_from_display_to_label() {
        val t = DefaultMoonTypographyTokens
        assertTrue(t.displayLargeSp > t.displayMediumSp)
        assertTrue(t.displayMediumSp > t.displaySmallSp)
        assertTrue(t.displaySmallSp > t.headlineLargeSp)
        assertTrue(t.headlineLargeSp > t.headlineMediumSp)
        assertTrue(t.headlineMediumSp > t.headlineSmallSp)
        assertTrue(t.headlineSmallSp > t.titleLargeSp)
        assertTrue(t.titleLargeSp > t.titleMediumSp)
        assertTrue(t.titleMediumSp > t.titleSmallSp)
        assertTrue(t.bodyLargeSp > t.bodyMediumSp)
        assertTrue(t.bodyMediumSp > t.bodySmallSp)
        assertTrue(t.labelLargeSp > t.labelMediumSp)
        assertTrue(t.labelMediumSp > t.labelSmallSp)
    }

    @Test
    fun custom_typography_overrides_defaults() {
        val custom = object : MoonTypographyTokens {
            override val bodyLargeSp: Int get() = 20
        }
        assertEquals(20, custom.bodyLargeSp)
        assertEquals(57, custom.displayLargeSp)
    }
}
