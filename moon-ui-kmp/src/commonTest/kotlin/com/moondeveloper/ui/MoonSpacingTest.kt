package com.moondeveloper.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MoonSpacingTest {

    @Test
    fun spacing_values_follow_8dp_grid() {
        assertEquals(8, MoonSpacing.SM)
        assertEquals(16, MoonSpacing.MD)
        assertEquals(24, MoonSpacing.LG)
        assertEquals(32, MoonSpacing.XL)
    }

    @Test
    fun spacing_sub_grid_values() {
        assertEquals(2, MoonSpacing.XXS)
        assertEquals(4, MoonSpacing.XS)
    }

    @Test
    fun spacing_large_values() {
        assertEquals(48, MoonSpacing.XXL)
        assertEquals(64, MoonSpacing.XXXL)
    }

    @Test
    fun spacing_is_strictly_increasing() {
        val values = listOf(
            MoonSpacing.XXS, MoonSpacing.XS, MoonSpacing.SM,
            MoonSpacing.MD, MoonSpacing.LG, MoonSpacing.XL,
            MoonSpacing.XXL, MoonSpacing.XXXL,
        )
        for (i in 0 until values.size - 1) {
            assertTrue(values[i] < values[i + 1], "spacing[$i]=${values[i]} should be < spacing[${i + 1}]=${values[i + 1]}")
        }
    }

    @Test
    fun md_is_double_sm() {
        assertEquals(MoonSpacing.SM * 2, MoonSpacing.MD)
    }
}
