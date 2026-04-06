package com.moondeveloper.maps

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeMapControllerTest {

    private val controller = FakeMapController()

    @Test
    fun initialState_noLocationAndNotTracking() = runTest {
        assertNull(controller.currentLocation.first())
        assertFalse(controller.isTracking.first())
        assertEquals(LocationPermissionState.NOT_DETERMINED, controller.permissionState.first())
    }

    @Test
    fun simulateLocation_updatesCurrentLocation() = runTest {
        val point = GeoPoint.SEOUL
        controller.simulateLocation(point)
        assertEquals(point, controller.currentLocation.first())
    }

    @Test
    fun startTracking_setsTrackingTrue() = runTest {
        controller.startTracking()
        assertTrue(controller.isTracking.first())
    }

    @Test
    fun stopTracking_setsTrackingFalse() = runTest {
        controller.startTracking()
        controller.stopTracking()
        assertFalse(controller.isTracking.first())
    }

    @Test
    fun trackingRoute_recordsPoints() {
        controller.startTracking()
        controller.simulateLocation(GeoPoint(37.5665, 126.9780))
        controller.simulateLocation(GeoPoint(37.5670, 126.9785))
        controller.simulateLocation(GeoPoint(37.5675, 126.9790))

        assertEquals(3, controller.recordedPointCount)

        val route = controller.stopTracking()
        assertEquals(3, route.pointCount)
        assertTrue(route.distanceMeters > 0)
    }

    @Test
    fun notTracking_doesNotRecordPoints() {
        // Simulate location without starting tracking
        controller.simulateLocation(GeoPoint.SEOUL)
        controller.simulateLocation(GeoPoint.BUSAN)

        assertEquals(0, controller.recordedPointCount)

        val route = controller.stopTracking()
        assertEquals(0, route.pointCount)
    }

    @Test
    fun stopTracking_clearsRecordedPoints() {
        controller.startTracking()
        controller.simulateLocation(GeoPoint.SEOUL)
        controller.stopTracking()

        // Start a new tracking session
        controller.startTracking()
        assertEquals(0, controller.recordedPointCount)
    }

    @Test
    fun hasLocationPermission_respectsPermissionState() = runTest {
        controller.simulatePermissionState(LocationPermissionState.GRANTED)
        assertTrue(controller.hasLocationPermission())

        controller.simulatePermissionState(LocationPermissionState.DENIED)
        assertFalse(controller.hasLocationPermission())
    }

    @Test
    fun requestLocationPermission_grantsWhenAllowed() = runTest {
        controller.permissionGranted = true
        val result = controller.requestLocationPermission()
        assertTrue(result)
        assertEquals(LocationPermissionState.GRANTED, controller.permissionState.first())
    }

    @Test
    fun requestLocationPermission_deniesWhenNotAllowed() = runTest {
        controller.permissionGranted = false
        val result = controller.requestLocationPermission()
        assertFalse(result)
        assertEquals(LocationPermissionState.DENIED, controller.permissionState.first())
    }

    @Test
    fun getLastKnownLocation_returnsSetValue() = runTest {
        controller.lastKnownLocation = GeoPoint.BUSAN
        assertEquals(GeoPoint.BUSAN, controller.getLastKnownLocation())
    }

    @Test
    fun getLastKnownLocation_defaultsToNull() = runTest {
        assertNull(controller.getLastKnownLocation())
    }

    @Test
    fun cameraPositionForRoute_returnsNullForEmptyRoute() {
        val route = WalkRoute.empty()
        assertNull(controller.cameraPositionForRoute(route))
    }

    @Test
    fun cameraPositionForRoute_centersBetweenPoints() {
        val points = listOf(
            GeoPoint(37.0, 127.0),
            GeoPoint(38.0, 127.0)
        )
        val route = WalkRoute.empty().copy(points = points, distanceMeters = 111000.0) // ~111km

        val camera = controller.cameraPositionForRoute(route)!!
        assertEquals(37.5, camera.center.latitude, 0.01)
        assertEquals(127.0, camera.center.longitude, 0.01)
    }

    @Test
    fun reset_clearsAllState() = runTest {
        controller.simulateLocation(GeoPoint.SEOUL)
        controller.simulatePermissionState(LocationPermissionState.GRANTED)
        controller.startTracking()
        controller.permissionGranted = false
        controller.lastKnownLocation = GeoPoint.BUSAN

        controller.reset()

        assertNull(controller.currentLocation.first())
        assertEquals(LocationPermissionState.NOT_DETERMINED, controller.permissionState.first())
        assertFalse(controller.isTracking.first())
        assertEquals(0, controller.recordedPointCount)
        assertTrue(controller.permissionGranted)
        assertNull(controller.lastKnownLocation)
    }
}
