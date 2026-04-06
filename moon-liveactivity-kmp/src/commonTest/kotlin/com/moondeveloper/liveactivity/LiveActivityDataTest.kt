package com.moondeveloper.liveactivity

import kotlin.test.Test
import kotlin.test.assertEquals

class LiveActivityDataTest {

    @Test
    fun defaultValues_areCorrect() {
        val data = LiveActivityData(title = "Test", primaryValue = "00:00")
        assertEquals("Test", data.title)
        assertEquals("", data.subtitle)
        assertEquals("00:00", data.primaryValue)
        assertEquals("", data.secondaryValue)
        assertEquals("", data.iconEmoji)
        assertEquals(0f, data.progressFraction)
        assertEquals(true, data.isOngoing)
    }

    @Test
    fun copy_preservesUnchangedValues() {
        val original = LiveActivityData(
            title = "Walking",
            subtitle = "With Max",
            primaryValue = "15:00",
            secondaryValue = "1.5km",
            iconEmoji = "dog",
            progressFraction = 0.5f,
            isOngoing = true
        )
        val copy = original.copy(primaryValue = "20:00")
        assertEquals("Walking", copy.title)
        assertEquals("With Max", copy.subtitle)
        assertEquals("20:00", copy.primaryValue)
        assertEquals("1.5km", copy.secondaryValue)
        assertEquals("dog", copy.iconEmoji)
        assertEquals(0.5f, copy.progressFraction)
        assertEquals(true, copy.isOngoing)
    }
}
