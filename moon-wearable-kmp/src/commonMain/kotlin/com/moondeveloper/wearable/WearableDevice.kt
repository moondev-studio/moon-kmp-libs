package com.moondeveloper.wearable

/**
 * Represents a wearable device that can be connected.
 *
 * @property id Unique identifier for the device
 * @property name Human-readable device name
 * @property type Type of wearable device
 * @property manufacturer Device manufacturer (if known)
 * @property model Device model (if known)
 * @property platformInfo Platform-specific information
 */
data class WearableDevice(
    val id: String,
    val name: String,
    val type: WearableType,
    val manufacturer: String? = null,
    val model: String? = null,
    val platformInfo: Map<String, String> = emptyMap()
)

/**
 * Type of wearable device.
 */
enum class WearableType {
    /** Smart watch (Wear OS, watchOS) */
    WATCH,

    /** Fitness band/tracker */
    FITNESS_BAND,

    /** Smart ring */
    RING,

    /** Earbuds with sensors */
    EARBUDS,

    /** Smart glasses */
    GLASSES,

    /** Other wearable device */
    OTHER,

    /** Unknown type */
    UNKNOWN
}

/**
 * Capabilities that a wearable device may support.
 */
enum class WearableCapability {
    /** Can receive messages from phone */
    RECEIVE_MESSAGES,

    /** Can send messages to phone */
    SEND_MESSAGES,

    /** Supports data sync via Data Layer */
    DATA_SYNC,

    /** Has heart rate sensor */
    HEART_RATE,

    /** Has step counter */
    STEPS,

    /** Has GPS */
    GPS,

    /** Has accelerometer */
    ACCELEROMETER,

    /** Has gyroscope */
    GYROSCOPE,

    /** Can make voice calls */
    VOICE_CALLS,

    /** Has microphone */
    MICROPHONE,

    /** Has speaker */
    SPEAKER,

    /** Supports app installation */
    APP_INSTALL,

    /** Supports tiles/complications */
    TILES,

    /** Has always-on display */
    ALWAYS_ON_DISPLAY,

    /** Has blood oxygen sensor */
    BLOOD_OXYGEN,

    /** Has ECG sensor */
    ECG,

    /** Has body temperature sensor */
    TEMPERATURE
}
