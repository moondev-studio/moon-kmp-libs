package com.moondeveloper.health

import kotlinx.datetime.Instant

/**
 * A single health data record.
 *
 * @property id Unique identifier for this record
 * @property type The type of health data
 * @property startTime When the measurement/activity started
 * @property endTime When the measurement/activity ended (same as startTime for instant measurements)
 * @property value Primary numeric value (interpretation depends on type)
 * @property unit Unit of measurement
 * @property metadata Additional type-specific data
 * @property source The app or device that recorded this data
 */
data class HealthRecord(
    val id: String,
    val type: HealthDataType,
    val startTime: Instant,
    val endTime: Instant,
    val value: Double,
    val unit: HealthUnit,
    val metadata: Map<String, Any> = emptyMap(),
    val source: DataSource? = null
)

/**
 * Units of measurement for health data.
 */
enum class HealthUnit {
    /** Beats per minute (heart rate) */
    BPM,

    /** Count (steps, floors) */
    COUNT,

    /** Meters */
    METERS,

    /** Kilometers */
    KILOMETERS,

    /** Kilocalories */
    KCAL,

    /** Percentage (SpO2) */
    PERCENT,

    /** Minutes */
    MINUTES,

    /** Hours */
    HOURS,

    /** Kilograms */
    KILOGRAMS,

    /** Centimeters */
    CENTIMETERS,

    /** Millimeters of mercury (blood pressure) */
    MMHG,

    /** Degrees Celsius */
    CELSIUS,

    /** Breaths per minute */
    BREATHS_PER_MIN,

    /** Milliseconds (HRV) */
    MILLISECONDS,

    /** Milliliters */
    MILLILITERS,

    /** Grams */
    GRAMS,

    /** No unit */
    NONE
}

/**
 * Information about the source of health data.
 */
data class DataSource(
    /** Package name or bundle ID of the source app */
    val packageName: String,

    /** Human-readable app name */
    val appName: String? = null,

    /** Device identifier (if from a specific device) */
    val deviceId: String? = null,

    /** Device type (phone, watch, etc.) */
    val deviceType: DeviceType? = null
)

/**
 * Type of device that recorded the data.
 */
enum class DeviceType {
    PHONE,
    WATCH,
    FITNESS_TRACKER,
    SCALE,
    BLOOD_PRESSURE_MONITOR,
    THERMOMETER,
    OTHER,
    UNKNOWN
}
