package com.moondeveloper.health

import kotlin.time.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class WorkoutDataTest {

    private val now = Clock.System.now()

    @Test
    fun workoutSession_calculatesDuration() {
        val session = WorkoutSession(
            type = WorkoutType.RUNNING,
            startTime = now.minus(45.minutes),
            endTime = now
        )

        assertEquals(45L, session.durationMinutes)
    }

    @Test
    fun workoutSession_createsWithAllFields() {
        val session = WorkoutSession(
            type = WorkoutType.CYCLING,
            startTime = now.minus(1.hours),
            endTime = now,
            activeCalories = 350.0,
            totalCalories = 420.0,
            distanceMeters = 20000.0,
            avgHeartRate = 145,
            maxHeartRate = 175
        )

        assertEquals(WorkoutType.CYCLING, session.type)
        assertEquals(350.0, session.activeCalories)
        assertEquals(420.0, session.totalCalories)
        assertEquals(20000.0, session.distanceMeters)
        assertNull(session.steps)
        assertEquals(145, session.avgHeartRate)
        assertEquals(175, session.maxHeartRate)
    }

    @Test
    fun workoutSession_createsWithMinimalFields() {
        val session = WorkoutSession(
            type = WorkoutType.YOGA,
            startTime = now.minus(30.minutes),
            endTime = now
        )

        assertEquals(WorkoutType.YOGA, session.type)
        assertEquals(0.0, session.activeCalories)
        assertNull(session.distanceMeters)
        assertNull(session.avgHeartRate)
        assertTrue(session.segments.isEmpty())
    }

    @Test
    fun workoutSegment_createsCorrectly() {
        val segment = WorkoutSegment(
            startTime = now.minus(10.minutes),
            endTime = now,
            distanceMeters = 1600.0,
            avgHeartRate = 160
        )

        assertEquals(1600.0, segment.distanceMeters)
        assertEquals(160, segment.avgHeartRate)
    }

    @Test
    fun workoutType_containsCardioTypes() {
        val allTypes = WorkoutType.entries

        assertTrue(WorkoutType.RUNNING in allTypes)
        assertTrue(WorkoutType.WALKING in allTypes)
        assertTrue(WorkoutType.CYCLING in allTypes)
        assertTrue(WorkoutType.SWIMMING in allTypes)
    }

    @Test
    fun workoutType_containsFitnessTypes() {
        val allTypes = WorkoutType.entries

        assertTrue(WorkoutType.STRENGTH_TRAINING in allTypes)
        assertTrue(WorkoutType.HIIT in allTypes)
        assertTrue(WorkoutType.YOGA in allTypes)
        assertTrue(WorkoutType.PILATES in allTypes)
    }

    @Test
    fun workoutType_containsSportsTypes() {
        val allTypes = WorkoutType.entries

        assertTrue(WorkoutType.BASKETBALL in allTypes)
        assertTrue(WorkoutType.SOCCER in allTypes)
        assertTrue(WorkoutType.TENNIS in allTypes)
    }

    @Test
    fun workoutSession_withSegments() {
        val segments = listOf(
            WorkoutSegment(now.minus(30.minutes), now.minus(20.minutes), 1600.0, 155),
            WorkoutSegment(now.minus(20.minutes), now.minus(10.minutes), 1600.0, 160),
            WorkoutSegment(now.minus(10.minutes), now, 1600.0, 165)
        )

        val session = WorkoutSession(
            type = WorkoutType.RUNNING,
            startTime = now.minus(30.minutes),
            endTime = now,
            activeCalories = 300.0,
            distanceMeters = 4800.0,
            segments = segments
        )

        assertEquals(3, session.segments.size)
        assertEquals(1600.0, session.segments[0].distanceMeters)
    }
}
