package com.moondeveloper.maps

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * A geographic coordinate point with optional altitude and timestamp.
 */
@OptIn(ExperimentalTime::class)
data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val timestamp: Instant = Clock.System.now()
) {
    init {
        require(latitude in -90.0..90.0) { "Latitude must be between -90 and 90" }
        require(longitude in -180.0..180.0) { "Longitude must be between -180 and 180" }
    }

    companion object {
        /** Seoul City Hall coordinates */
        val SEOUL = GeoPoint(37.5665, 126.9780)

        /** Busan Station coordinates */
        val BUSAN = GeoPoint(35.1152, 129.0421)
    }
}

/**
 * A recorded walk/tracking route with computed statistics.
 */
data class WalkRoute(
    val points: List<GeoPoint>,
    val distanceMeters: Double,
    val durationSeconds: Long,
    val averageSpeedKmh: Double,
    val startTime: Instant,
    val endTime: Instant
) {
    val pointCount: Int get() = points.size

    val distanceKilometers: Double get() = distanceMeters / 1000.0

    companion object {
        @OptIn(ExperimentalTime::class)
        fun empty() = WalkRoute(
            points = emptyList(),
            distanceMeters = 0.0,
            durationSeconds = 0L,
            averageSpeedKmh = 0.0,
            startTime = Clock.System.now(),
            endTime = Clock.System.now()
        )
    }
}

/**
 * Map camera position with center point and zoom level.
 */
data class CameraPosition(
    val center: GeoPoint,
    val zoomLevel: Float = 15f
) {
    init {
        require(zoomLevel in 1f..22f) { "Zoom level must be between 1 and 22" }
    }
}

/**
 * Location permission state.
 */
enum class LocationPermissionState {
    /** Permission granted (always or when-in-use) */
    GRANTED,
    /** Permission explicitly denied by user */
    DENIED,
    /** Permission not yet requested */
    NOT_DETERMINED,
    /** Permission restricted by device policy */
    RESTRICTED
}

/**
 * Location tracking accuracy level.
 */
enum class LocationAccuracy {
    /** High accuracy (GPS + network) - higher battery usage */
    HIGH,
    /** Balanced accuracy (network primarily) */
    BALANCED,
    /** Low accuracy (passive updates only) - minimal battery usage */
    LOW
}

/**
 * Location update configuration.
 */
data class LocationConfig(
    val accuracy: LocationAccuracy = LocationAccuracy.HIGH,
    val minIntervalMs: Long = 3000L,
    val minDistanceMeters: Float = 5f
) {
    init {
        require(minIntervalMs >= 1000L) { "Minimum interval must be at least 1000ms" }
        require(minDistanceMeters >= 0f) { "Minimum distance cannot be negative" }
    }
}
