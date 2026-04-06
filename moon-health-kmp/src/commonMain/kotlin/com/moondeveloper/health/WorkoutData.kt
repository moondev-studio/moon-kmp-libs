package com.moondeveloper.health

import kotlinx.datetime.Instant

/**
 * Detailed workout session data.
 *
 * Use with [HealthRecord] where type is [HealthDataType.WORKOUT].
 * The [HealthRecord.value] represents active calories burned.
 */
data class WorkoutSession(
    val type: WorkoutType,
    val startTime: Instant,
    val endTime: Instant,
    val activeCalories: Double = 0.0,
    val totalCalories: Double = 0.0,
    val distanceMeters: Double? = null,
    val steps: Int? = null,
    val avgHeartRate: Int? = null,
    val maxHeartRate: Int? = null,
    val segments: List<WorkoutSegment> = emptyList()
) {
    /** Duration in minutes */
    val durationMinutes: Long
        get() = (endTime.toEpochMilliseconds() - startTime.toEpochMilliseconds()) / 60_000
}

/**
 * A segment within a workout (e.g., lap, interval).
 */
data class WorkoutSegment(
    val startTime: Instant,
    val endTime: Instant,
    val distanceMeters: Double? = null,
    val avgHeartRate: Int? = null
)

/**
 * Types of workouts.
 */
enum class WorkoutType {
    // Cardio
    RUNNING,
    WALKING,
    CYCLING,
    SWIMMING,
    HIKING,
    ELLIPTICAL,
    ROWING,
    STAIR_CLIMBING,

    // Sports
    BASKETBALL,
    SOCCER,
    TENNIS,
    BADMINTON,
    GOLF,
    VOLLEYBALL,
    TABLE_TENNIS,

    // Fitness
    STRENGTH_TRAINING,
    FUNCTIONAL_TRAINING,
    HIIT,
    PILATES,
    YOGA,
    DANCE,
    MARTIAL_ARTS,
    BOXING,
    STRETCHING,
    CORE_TRAINING,

    // Winter Sports
    SKIING,
    SNOWBOARDING,
    ICE_SKATING,

    // Water Sports
    SURFING,
    WATER_POLO,
    KAYAKING,

    // Mind & Body
    MEDITATION,
    BREATHING,

    // Other
    WHEELCHAIR,
    OTHER,
    UNKNOWN
}
