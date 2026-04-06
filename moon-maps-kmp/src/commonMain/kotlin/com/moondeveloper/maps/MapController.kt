package com.moondeveloper.maps

import kotlinx.coroutines.flow.Flow

/**
 * Platform-agnostic map and location controller.
 *
 * Provides GPS tracking for walk/exercise routes.
 * Implementations use platform-specific location services:
 * - Android: Google Play Services Location / FusedLocationProvider
 * - iOS: CoreLocation / CLLocationManager
 *
 * @see FakeMapController for unit testing
 * @see NoOpMapController for unsupported platforms
 */
interface MapController {

    /** Reactive stream of current location updates, or `null` if unavailable. */
    val currentLocation: Flow<GeoPoint?>

    /** Current permission state for location access. */
    val permissionState: Flow<LocationPermissionState>

    /** Whether GPS tracking is currently active. */
    val isTracking: Flow<Boolean>

    /**
     * Start GPS tracking for walk/route recording.
     * Location updates will be accumulated for route calculation.
     * @param config Location update configuration
     */
    fun startTracking(config: LocationConfig = LocationConfig())

    /**
     * Stop GPS tracking and return the recorded route.
     * @return Recorded walk route with distance, duration, and points
     */
    fun stopTracking(): WalkRoute

    /** Check if location permission is currently granted. */
    fun hasLocationPermission(): Boolean

    /**
     * Request location permission from the user.
     * Implementation is platform-specific (ActivityResultLauncher on Android, CLLocationManager on iOS).
     * @return `true` if permission granted, `false` otherwise
     */
    suspend fun requestLocationPermission(): Boolean

    /**
     * Get the last known location without starting continuous updates.
     * May return `null` if no location is available.
     */
    suspend fun getLastKnownLocation(): GeoPoint?

    /**
     * Calculate optimal camera position to show the given route.
     * @param route Route to display
     * @param paddingPercent Padding around route as percentage (0.0-0.5)
     * @return Camera position centered on route, or null if route is empty
     */
    fun cameraPositionForRoute(route: WalkRoute, paddingPercent: Float = 0.1f): CameraPosition?
}
