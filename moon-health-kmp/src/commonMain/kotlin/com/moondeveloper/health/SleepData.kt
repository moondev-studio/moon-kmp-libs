package com.moondeveloper.health

import kotlinx.datetime.Instant

/**
 * Detailed sleep session data.
 *
 * Use with [HealthRecord] where type is [HealthDataType.SLEEP].
 * The [HealthRecord.value] represents total sleep duration in minutes.
 */
data class SleepSession(
    val startTime: Instant,
    val endTime: Instant,
    val stages: List<SleepStage> = emptyList()
) {
    /** Total sleep duration in minutes */
    val durationMinutes: Long
        get() = (endTime.toEpochMilliseconds() - startTime.toEpochMilliseconds()) / 60_000

    /** Duration of each stage type in minutes */
    val stageDurations: Map<SleepStageType, Long>
        get() = stages.groupBy { it.type }
            .mapValues { (_, stages) ->
                stages.sumOf { it.durationMinutes }
            }
}

/**
 * A single sleep stage within a sleep session.
 */
data class SleepStage(
    val type: SleepStageType,
    val startTime: Instant,
    val endTime: Instant
) {
    val durationMinutes: Long
        get() = (endTime.toEpochMilliseconds() - startTime.toEpochMilliseconds()) / 60_000
}

/**
 * Types of sleep stages.
 */
enum class SleepStageType {
    /** Awake during sleep session */
    AWAKE,

    /** Light sleep (N1/N2) */
    LIGHT,

    /** Deep sleep (N3) */
    DEEP,

    /** REM sleep */
    REM,

    /** Out of bed */
    OUT_OF_BED,

    /** Sleeping but stage unknown */
    SLEEPING,

    /** Unknown stage */
    UNKNOWN
}
