package com.moondeveloper.health

import kotlin.time.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class SleepDataTest {

    private val now = Clock.System.now()

    @Test
    fun sleepSession_calculatesDuration() {
        val session = SleepSession(
            startTime = now.minus(8.hours),
            endTime = now
        )

        assertEquals(480L, session.durationMinutes) // 8 hours = 480 minutes
    }

    @Test
    fun sleepSession_aggregatesStageDurations() {
        val session = SleepSession(
            startTime = now.minus(8.hours),
            endTime = now,
            stages = listOf(
                SleepStage(SleepStageType.LIGHT, now.minus(8.hours), now.minus(7.hours)),
                SleepStage(SleepStageType.DEEP, now.minus(7.hours), now.minus(5.hours)),
                SleepStage(SleepStageType.REM, now.minus(5.hours), now.minus(4.hours)),
                SleepStage(SleepStageType.LIGHT, now.minus(4.hours), now.minus(2.hours)),
                SleepStage(SleepStageType.DEEP, now.minus(2.hours), now.minus(1.hours)),
                SleepStage(SleepStageType.LIGHT, now.minus(1.hours), now)
            )
        )

        val durations = session.stageDurations

        assertEquals(240L, durations[SleepStageType.LIGHT]) // 4 hours
        assertEquals(180L, durations[SleepStageType.DEEP])  // 3 hours
        assertEquals(60L, durations[SleepStageType.REM])    // 1 hour
    }

    @Test
    fun sleepStage_calculatesDuration() {
        val stage = SleepStage(
            type = SleepStageType.DEEP,
            startTime = now.minus(90.minutes),
            endTime = now
        )

        assertEquals(90L, stage.durationMinutes)
    }

    @Test
    fun sleepStageType_containsExpectedStages() {
        val allStages = SleepStageType.entries

        assertTrue(SleepStageType.AWAKE in allStages)
        assertTrue(SleepStageType.LIGHT in allStages)
        assertTrue(SleepStageType.DEEP in allStages)
        assertTrue(SleepStageType.REM in allStages)
    }

    @Test
    fun sleepSession_emptyStagesReturnsEmptyMap() {
        val session = SleepSession(
            startTime = now.minus(8.hours),
            endTime = now
        )

        assertTrue(session.stageDurations.isEmpty())
    }
}
