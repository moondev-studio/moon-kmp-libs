package com.moondeveloper.ui

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class WindowSizeClassTest {

    @Test
    fun compact_width_below_600dp() {
        val sizeClass = calculateWindowSizeClass(width = 599.dp, height = 800.dp)
        assertEquals(WindowWidthClass.Compact, sizeClass.width)
    }

    @Test
    fun medium_width_at_600dp() {
        val sizeClass = calculateWindowSizeClass(width = 600.dp, height = 800.dp)
        assertEquals(WindowWidthClass.Medium, sizeClass.width)
    }

    @Test
    fun medium_width_below_840dp() {
        val sizeClass = calculateWindowSizeClass(width = 839.dp, height = 800.dp)
        assertEquals(WindowWidthClass.Medium, sizeClass.width)
    }

    @Test
    fun expanded_width_at_840dp() {
        val sizeClass = calculateWindowSizeClass(width = 840.dp, height = 800.dp)
        assertEquals(WindowWidthClass.Expanded, sizeClass.width)
    }

    @Test
    fun compact_height_below_480dp() {
        val sizeClass = calculateWindowSizeClass(width = 400.dp, height = 479.dp)
        assertEquals(WindowHeightClass.Compact, sizeClass.height)
    }

    @Test
    fun medium_height_at_480dp() {
        val sizeClass = calculateWindowSizeClass(width = 400.dp, height = 480.dp)
        assertEquals(WindowHeightClass.Medium, sizeClass.height)
    }

    @Test
    fun expanded_height_at_900dp() {
        val sizeClass = calculateWindowSizeClass(width = 400.dp, height = 900.dp)
        assertEquals(WindowHeightClass.Expanded, sizeClass.height)
    }

    @Test
    fun phone_portrait_typical() {
        val sizeClass = calculateWindowSizeClass(width = 360.dp, height = 640.dp)
        assertEquals(WindowWidthClass.Compact, sizeClass.width)
        assertEquals(WindowHeightClass.Medium, sizeClass.height)
    }

    @Test
    fun tablet_landscape_typical() {
        val sizeClass = calculateWindowSizeClass(width = 1024.dp, height = 768.dp)
        assertEquals(WindowWidthClass.Expanded, sizeClass.width)
        assertEquals(WindowHeightClass.Medium, sizeClass.height)
    }
}
