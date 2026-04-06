package com.moondeveloper.maps

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Geographic calculation utilities.
 * Uses Haversine formula for distance calculations on Earth's surface.
 */
object GeoUtils {

    private const val EARTH_RADIUS_METERS = 6371000.0

    /**
     * Calculate distance between two geographic points using Haversine formula.
     * @return Distance in meters
     */
    fun distanceBetween(from: GeoPoint, to: GeoPoint): Double {
        val lat1 = from.latitude.toRadians()
        val lat2 = to.latitude.toRadians()
        val dLat = (to.latitude - from.latitude).toRadians()
        val dLon = (to.longitude - from.longitude).toRadians()

        val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_METERS * c
    }

    /**
     * Calculate total distance of a route (sum of all segment distances).
     * @return Total distance in meters, or 0 if fewer than 2 points
     */
    fun totalDistance(points: List<GeoPoint>): Double {
        if (points.size < 2) return 0.0
        return points.zipWithNext { a, b -> distanceBetween(a, b) }.sum()
    }

    /**
     * Calculate average speed from distance and duration.
     * @param distanceMeters Distance in meters
     * @param durationSeconds Duration in seconds
     * @return Speed in km/h, or 0 if duration is 0
     */
    fun averageSpeed(distanceMeters: Double, durationSeconds: Long): Double {
        if (durationSeconds == 0L) return 0.0
        val distanceKm = distanceMeters / 1000.0
        val durationHours = durationSeconds / 3600.0
        return distanceKm / durationHours
    }

    /**
     * Estimate calories burned from walking distance.
     * Uses simplified MET-based formula: ~0.57 kcal/kg/km for walking.
     *
     * @param distanceMeters Distance walked in meters
     * @param weightKg Body weight in kg (default 60kg)
     * @return Estimated calories burned
     */
    fun estimateCalories(distanceMeters: Double, weightKg: Double = 60.0): Double {
        require(weightKg > 0) { "Weight must be positive" }
        val distanceKm = distanceMeters / 1000.0
        return distanceKm * weightKg * 0.57
    }

    /**
     * Calculate pace (minutes per kilometer) from speed.
     * @param speedKmh Speed in km/h
     * @return Pace in minutes per km, or null if speed is 0
     */
    fun pace(speedKmh: Double): Double? {
        if (speedKmh <= 0) return null
        return 60.0 / speedKmh
    }

    /**
     * Format pace as "mm:ss" string.
     */
    fun formatPace(paceMinPerKm: Double): String {
        val totalSeconds = (paceMinPerKm * 60).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "$minutes:${seconds.toString().padStart(2, '0')}"
    }

    /**
     * Get center point of a list of coordinates (simple centroid).
     */
    fun center(points: List<GeoPoint>): GeoPoint? {
        if (points.isEmpty()) return null
        val avgLat = points.map { it.latitude }.average()
        val avgLon = points.map { it.longitude }.average()
        return GeoPoint(avgLat, avgLon)
    }

    /**
     * Check if a point is within a circular region.
     * @param point Point to check
     * @param center Center of the region
     * @param radiusMeters Radius in meters
     */
    fun isWithinRadius(point: GeoPoint, center: GeoPoint, radiusMeters: Double): Boolean {
        return distanceBetween(point, center) <= radiusMeters
    }

    private fun Double.toRadians(): Double = this * kotlin.math.PI / 180.0
}
