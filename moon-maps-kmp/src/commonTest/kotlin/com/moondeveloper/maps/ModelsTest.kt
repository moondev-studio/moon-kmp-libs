package com.moondeveloper.maps

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ModelsTest {

    // --- GeoPoint Tests ---

    @Test
    fun geoPoint_validCoordinates_createsSuccessfully() {
        val point = GeoPoint(37.5665, 126.9780)
        assertEquals(37.5665, point.latitude)
        assertEquals(126.9780, point.longitude)
        assertEquals(0.0, point.altitude)
    }

    @Test
    fun geoPoint_withAltitude_preservesValue() {
        val point = GeoPoint(37.5665, 126.9780, altitude = 100.5)
        assertEquals(100.5, point.altitude)
    }

    @Test
    fun geoPoint_invalidLatitude_throwsException() {
        assertFailsWith<IllegalArgumentException> {
            GeoPoint(91.0, 126.9780) // latitude > 90
        }
        assertFailsWith<IllegalArgumentException> {
            GeoPoint(-91.0, 126.9780) // latitude < -90
        }
    }

    @Test
    fun geoPoint_invalidLongitude_throwsException() {
        assertFailsWith<IllegalArgumentException> {
            GeoPoint(37.5665, 181.0) // longitude > 180
        }
        assertFailsWith<IllegalArgumentException> {
            GeoPoint(37.5665, -181.0) // longitude < -180
        }
    }

    @Test
    fun geoPoint_extremeValidValues_createsSuccessfully() {
        val northPole = GeoPoint(90.0, 0.0)
        val southPole = GeoPoint(-90.0, 0.0)
        val dateLine = GeoPoint(0.0, 180.0)
        val dateLineNeg = GeoPoint(0.0, -180.0)

        assertEquals(90.0, northPole.latitude)
        assertEquals(-90.0, southPole.latitude)
        assertEquals(180.0, dateLine.longitude)
        assertEquals(-180.0, dateLineNeg.longitude)
    }

    // --- WalkRoute Tests ---

    @Test
    fun walkRoute_empty_hasZeroValues() {
        val route = WalkRoute.empty()
        assertEquals(0, route.pointCount)
        assertEquals(0.0, route.distanceMeters)
        assertEquals(0L, route.durationSeconds)
        assertEquals(0.0, route.averageSpeedKmh)
    }

    @Test
    fun walkRoute_distanceKilometers_convertsCorrectly() {
        val route = WalkRoute.empty().copy(distanceMeters = 5500.0)
        assertEquals(5.5, route.distanceKilometers, 0.001)
    }

    // --- CameraPosition Tests ---

    @Test
    fun cameraPosition_defaultZoom_is15() {
        val position = CameraPosition(GeoPoint.SEOUL)
        assertEquals(15f, position.zoomLevel)
    }

    @Test
    fun cameraPosition_customZoom_preservesValue() {
        val position = CameraPosition(GeoPoint.SEOUL, zoomLevel = 18f)
        assertEquals(18f, position.zoomLevel)
    }

    @Test
    fun cameraPosition_invalidZoom_throwsException() {
        assertFailsWith<IllegalArgumentException> {
            CameraPosition(GeoPoint.SEOUL, zoomLevel = 0f) // < 1
        }
        assertFailsWith<IllegalArgumentException> {
            CameraPosition(GeoPoint.SEOUL, zoomLevel = 25f) // > 22
        }
    }

    // --- LocationConfig Tests ---

    @Test
    fun locationConfig_defaultValues() {
        val config = LocationConfig()
        assertEquals(LocationAccuracy.HIGH, config.accuracy)
        assertEquals(3000L, config.minIntervalMs)
        assertEquals(5f, config.minDistanceMeters)
    }

    @Test
    fun locationConfig_invalidInterval_throwsException() {
        assertFailsWith<IllegalArgumentException> {
            LocationConfig(minIntervalMs = 500L) // < 1000ms
        }
    }

    @Test
    fun locationConfig_invalidDistance_throwsException() {
        assertFailsWith<IllegalArgumentException> {
            LocationConfig(minDistanceMeters = -1f) // negative
        }
    }
}
