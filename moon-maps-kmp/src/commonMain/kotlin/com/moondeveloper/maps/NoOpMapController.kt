package com.moondeveloper.maps

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * No-operation [MapController] for unsupported platforms (e.g., desktop).
 * All operations are safe no-ops that return empty/default values.
 */
class NoOpMapController : MapController {

    override val currentLocation: Flow<GeoPoint?> = MutableStateFlow(null)

    override val permissionState: Flow<LocationPermissionState> =
        MutableStateFlow(LocationPermissionState.RESTRICTED)

    override val isTracking: Flow<Boolean> = MutableStateFlow(false)

    override fun startTracking(config: LocationConfig) {
        // No-op: GPS not available on this platform
    }

    override fun stopTracking(): WalkRoute = WalkRoute.empty()

    override fun hasLocationPermission(): Boolean = false

    override suspend fun requestLocationPermission(): Boolean = false

    override suspend fun getLastKnownLocation(): GeoPoint? = null

    override fun cameraPositionForRoute(route: WalkRoute, paddingPercent: Float): CameraPosition? = null
}
