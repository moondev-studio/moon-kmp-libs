package com.moondeveloper.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MoonShapeTest {

    @Test
    fun shape_none_is_zero() {
        assertEquals(0, MoonShape.NONE)
    }

    @Test
    fun shape_full_is_50_percent() {
        assertEquals(50, MoonShape.FULL)
    }

    @Test
    fun shape_md_is_card_standard_12dp() {
        assertEquals(12, MoonShape.MD)
    }

    @Test
    fun shape_values_are_correct() {
        assertEquals(4, MoonShape.XS)
        assertEquals(8, MoonShape.SM)
        assertEquals(16, MoonShape.LG)
        assertEquals(28, MoonShape.XL)
    }

    @Test
    fun shape_is_strictly_increasing() {
        val values = listOf(
            MoonShape.NONE, MoonShape.XS, MoonShape.SM,
            MoonShape.MD, MoonShape.LG, MoonShape.XL, MoonShape.FULL,
        )
        for (i in 0 until values.size - 1) {
            assertTrue(values[i] < values[i + 1], "shape[$i]=${values[i]} should be < shape[${i + 1}]=${values[i + 1]}")
        }
    }
}
