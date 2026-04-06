package com.moondeveloper.maps

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GeoUtilsTest {

    @Test
    fun distanceBetween_samePoint_returnsZero() {
        val point = GeoPoint.SEOUL
        val distance = GeoUtils.distanceBetween(point, point)
        assertEquals(0.0, distance, 0.01)
    }

    @Test
    fun distanceBetween_seoulToBusan_returnsApprox325km() {
        val distance = GeoUtils.distanceBetween(GeoPoint.SEOUL, GeoPoint.BUSAN)
        // Seoul to Busan is approximately 325km
        assertTrue(distance > 320_000 && distance < 330_000, "Expected ~325km, got ${distance / 1000}km")
    }

    @Test
    fun distanceBetween_shortDistance_calculatesCorrectly() {
        val from = GeoPoint(37.5665, 126.9780)
        val to = GeoPoint(37.5675, 126.9790) // ~130m away
        val distance = GeoUtils.distanceBetween(from, to)
        assertTrue(distance > 100 && distance < 200, "Expected ~130m, got ${distance}m")
    }

    @Test
    fun totalDistance_emptyList_returnsZero() {
        assertEquals(0.0, GeoUtils.totalDistance(emptyList()))
    }

    @Test
    fun totalDistance_singlePoint_returnsZero() {
        assertEquals(0.0, GeoUtils.totalDistance(listOf(GeoPoint.SEOUL)))
    }

    @Test
    fun totalDistance_multiplePoints_sumOfSegments() {
        val points = listOf(
            GeoPoint(37.5665, 126.9780),
            GeoPoint(37.5670, 126.9785),
            GeoPoint(37.5675, 126.9790)
        )
        val total = GeoUtils.totalDistance(points)
        val segment1 = GeoUtils.distanceBetween(points[0], points[1])
        val segment2 = GeoUtils.distanceBetween(points[1], points[2])
        assertEquals(segment1 + segment2, total, 0.01)
    }

    @Test
    fun averageSpeed_zeroDuration_returnsZero() {
        assertEquals(0.0, GeoUtils.averageSpeed(1000.0, 0L))
    }

    @Test
    fun averageSpeed_1kmIn1Hour_returns1kmh() {
        val speed = GeoUtils.averageSpeed(1000.0, 3600L)
        assertEquals(1.0, speed, 0.01)
    }

    @Test
    fun averageSpeed_5kmIn30Minutes_returns10kmh() {
        val speed = GeoUtils.averageSpeed(5000.0, 1800L) // 30 minutes = 1800 seconds
        assertEquals(10.0, speed, 0.01)
    }

    @Test
    fun estimateCalories_1kmWith60kg_returns34kcal() {
        val calories = GeoUtils.estimateCalories(1000.0, 60.0)
        // 1km * 60kg * 0.57 = 34.2 kcal
        assertEquals(34.2, calories, 0.5)
    }

    @Test
    fun estimateCalories_defaultWeight() {
        val calories = GeoUtils.estimateCalories(2000.0) // default 60kg
        // 2km * 60kg * 0.57 = 68.4 kcal
        assertEquals(68.4, calories, 0.5)
    }

    @Test
    fun pace_5kmh_returns12minPerKm() {
        val pace = GeoUtils.pace(5.0)
        assertEquals(12.0, pace!!, 0.01)
    }

    @Test
    fun pace_zeroSpeed_returnsNull() {
        assertNull(GeoUtils.pace(0.0))
    }

    @Test
    fun formatPace_6minPerKm_returns6colon00() {
        val formatted = GeoUtils.formatPace(6.0)
        assertEquals("6:00", formatted)
    }

    @Test
    fun formatPace_6point5minPerKm_returns6colon30() {
        val formatted = GeoUtils.formatPace(6.5)
        assertEquals("6:30", formatted)
    }

    @Test
    fun center_emptyList_returnsNull() {
        assertNull(GeoUtils.center(emptyList()))
    }

    @Test
    fun center_singlePoint_returnsSamePoint() {
        val point = GeoPoint.SEOUL
        val center = GeoUtils.center(listOf(point))!!
        assertEquals(point.latitude, center.latitude, 0.0001)
        assertEquals(point.longitude, center.longitude, 0.0001)
    }

    @Test
    fun center_multiplePoints_returnsCentroid() {
        val points = listOf(
            GeoPoint(37.0, 127.0),
            GeoPoint(38.0, 127.0),
            GeoPoint(37.5, 128.0)
        )
        val center = GeoUtils.center(points)!!
        assertEquals(37.5, center.latitude, 0.01)
        assertEquals(127.33, center.longitude, 0.01)
    }

    @Test
    fun isWithinRadius_pointInside_returnsTrue() {
        val center = GeoPoint.SEOUL
        val nearby = GeoPoint(37.5666, 126.9781) // ~15m away
        assertTrue(GeoUtils.isWithinRadius(nearby, center, 100.0))
    }

    @Test
    fun isWithinRadius_pointOutside_returnsFalse() {
        val center = GeoPoint.SEOUL
        val farAway = GeoPoint.BUSAN
        assertTrue(!GeoUtils.isWithinRadius(farAway, center, 10_000.0))
    }
}
