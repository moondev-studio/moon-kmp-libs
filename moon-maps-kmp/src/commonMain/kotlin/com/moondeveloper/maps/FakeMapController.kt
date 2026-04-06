package com.moondeveloper.maps

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Fake [MapController] for unit testing.
 * Allows simulating location updates and permission states without platform dependencies.
 *
 * Usage:
 * ```kotlin
 * val controller = FakeMapController()
 * controller.permissionGranted = true
 * controller.startTracking()
 * controller.simulateLocation(GeoPoint(37.5665, 126.9780))
 * controller.simulateLocation(GeoPoint(37.5670, 126.9785))
 * val route = controller.stopTracking()
 * ```
 */
@OptIn(ExperimentalTime::class)
class FakeMapController : MapController {

    private val _currentLocation = MutableStateFlow<GeoPoint?>(null)
    override val currentLocation: Flow<GeoPoint?> = _currentLocation.asStateFlow()

    private val _permissionState = MutableStateFlow(LocationPermissionState.NOT_DETERMINED)
    override val permissionState: Flow<LocationPermissionState> = _permissionState.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    override val isTracking: Flow<Boolean> = _isTracking.asStateFlow()

    private val recordedPoints = mutableListOf<GeoPoint>()
    private var trackingStartTime: Instant? = null

    /** Set to control permission grant behavior. */
    var permissionGranted = true

    /** Last known location for testing. */
    var lastKnownLocation: GeoPoint? = null

    override fun startTracking(config: LocationConfig) {
        recordedPoints.clear()
        trackingStartTime = Clock.System.now()
        _isTracking.value = true
    }

    override fun stopTracking(): WalkRoute {
        _isTracking.value = false
        val endTime = Clock.System.now()
        val startTime = trackingStartTime ?: endTime

        val distance = GeoUtils.totalDistance(recordedPoints)
        val durationSeconds = (endTime.toEpochMilliseconds() - startTime.toEpochMilliseconds()) / 1000

        return WalkRoute(
            points = recordedPoints.toList(),
            distanceMeters = distance,
            durationSeconds = durationSeconds,
            averageSpeedKmh = GeoUtils.averageSpeed(distance, durationSeconds),
            startTime = startTime,
            endTime = endTime
        ).also {
            recordedPoints.clear()
            trackingStartTime = null
        }
    }

    override fun hasLocationPermission(): Boolean {
        return _permissionState.value == LocationPermissionState.GRANTED
    }

    override suspend fun requestLocationPermission(): Boolean {
        _permissionState.value = if (permissionGranted) {
            LocationPermissionState.GRANTED
        } else {
            LocationPermissionState.DENIED
        }
        return permissionGranted
    }

    override suspend fun getLastKnownLocation(): GeoPoint? = lastKnownLocation

    override fun cameraPositionForRoute(route: WalkRoute, paddingPercent: Float): CameraPosition? {
        val center = GeoUtils.center(route.points) ?: return null
        // Simplified zoom calculation based on route spread
        val zoomLevel = when {
            route.distanceMeters > 10000 -> 12f
            route.distanceMeters > 5000 -> 13f
            route.distanceMeters > 2000 -> 14f
            route.distanceMeters > 500 -> 15f
            else -> 16f
        }
        return CameraPosition(center, zoomLevel)
    }

    // --- Test helpers ---

    /**
     * Simulate a location update.
     * If tracking is active, the point is added to the route.
     */
    fun simulateLocation(point: GeoPoint) {
        _currentLocation.value = point
        if (_isTracking.value) {
            recordedPoints.add(point)
        }
    }

    /**
     * Simulate permission state change.
     */
    fun simulatePermissionState(state: LocationPermissionState) {
        _permissionState.value = state
    }

    /**
     * Clear all state for test isolation.
     */
    fun reset() {
        _currentLocation.value = null
        _permissionState.value = LocationPermissionState.NOT_DETERMINED
        _isTracking.value = false
        recordedPoints.clear()
        trackingStartTime = null
        permissionGranted = true
        lastKnownLocation = null
    }

    /** Get count of recorded points (for test assertions). */
    val recordedPointCount: Int get() = recordedPoints.size
}
