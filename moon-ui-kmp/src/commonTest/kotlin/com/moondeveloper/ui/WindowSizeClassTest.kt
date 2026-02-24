package com.moondeveloper.ui

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class WindowSizeClassTest {

    @Test
    fun compact_width_when_below_600dp() {
        val result = calculateWindowSizeClass(599.dp, 800.dp)
        assertEquals(WindowWidthClass.Compact, result.width)
    }

    @Test
    fun medium_width_when_between_600_and_840dp() {
        val result = calculateWindowSizeClass(600.dp, 800.dp)
        assertEquals(WindowWidthClass.Medium, result.width)

        val result2 = calculateWindowSizeClass(839.dp, 800.dp)
        assertEquals(WindowWidthClass.Medium, result2.width)
    }

    @Test
    fun expanded_width_when_840dp_or_above() {
        val result = calculateWindowSizeClass(840.dp, 800.dp)
        assertEquals(WindowWidthClass.Expanded, result.width)

        val result2 = calculateWindowSizeClass(1200.dp, 800.dp)
        assertEquals(WindowWidthClass.Expanded, result2.width)
    }

    @Test
    fun compact_height_when_below_480dp() {
        val result = calculateWindowSizeClass(400.dp, 479.dp)
        assertEquals(WindowHeightClass.Compact, result.height)
    }

    @Test
    fun medium_height_when_between_480_and_900dp() {
        val result = calculateWindowSizeClass(400.dp, 480.dp)
        assertEquals(WindowHeightClass.Medium, result.height)

        val result2 = calculateWindowSizeClass(400.dp, 899.dp)
        assertEquals(WindowHeightClass.Medium, result2.height)
    }

    @Test
    fun expanded_height_when_900dp_or_above() {
        val result = calculateWindowSizeClass(400.dp, 900.dp)
        assertEquals(WindowHeightClass.Expanded, result.height)
    }

    @Test
    fun typical_phone_dimensions() {
        val result = calculateWindowSizeClass(360.dp, 640.dp)
        assertEquals(WindowWidthClass.Compact, result.width)
        assertEquals(WindowHeightClass.Medium, result.height)
    }

    @Test
    fun typical_tablet_dimensions() {
        val result = calculateWindowSizeClass(768.dp, 1024.dp)
        assertEquals(WindowWidthClass.Medium, result.width)
        assertEquals(WindowHeightClass.Expanded, result.height)
    }

    @Test
    fun typical_desktop_dimensions() {
        val result = calculateWindowSizeClass(1920.dp, 1080.dp)
        assertEquals(WindowWidthClass.Expanded, result.width)
        assertEquals(WindowHeightClass.Expanded, result.height)
    }
}
