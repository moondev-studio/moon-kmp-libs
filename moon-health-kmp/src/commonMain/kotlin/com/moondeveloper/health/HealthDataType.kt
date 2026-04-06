package com.moondeveloper.health

/**
 * Supported health data types across platforms.
 *
 * Maps to HealthConnect record types on Android and HealthKit types on iOS.
 */
enum class HealthDataType {
    /** Heart rate in beats per minute */
    HEART_RATE,

    /** Step count */
    STEPS,

    /** Distance traveled in meters */
    DISTANCE,

    /** Active calories burned */
    ACTIVE_CALORIES,

    /** Total calories burned (active + basal) */
    TOTAL_CALORIES,

    /** Blood oxygen saturation percentage (SpO2) */
    BLOOD_OXYGEN,

    /** Sleep session data */
    SLEEP,

    /** Workout/exercise session */
    WORKOUT,

    /** Body weight in kilograms */
    WEIGHT,

    /** Body height in centimeters */
    HEIGHT,

    /** Blood pressure (systolic/diastolic) */
    BLOOD_PRESSURE,

    /** Body temperature in Celsius */
    BODY_TEMPERATURE,

    /** Respiratory rate in breaths per minute */
    RESPIRATORY_RATE,

    /** Resting heart rate */
    RESTING_HEART_RATE,

    /** Heart rate variability (SDNN in milliseconds) */
    HEART_RATE_VARIABILITY,

    /** Floors climbed */
    FLOORS_CLIMBED,

    /** Standing hours */
    STAND_HOURS,

    /** Mindfulness/meditation minutes */
    MINDFULNESS,

    /** Water intake in milliliters */
    HYDRATION,

    /** Nutrition data */
    NUTRITION
}
