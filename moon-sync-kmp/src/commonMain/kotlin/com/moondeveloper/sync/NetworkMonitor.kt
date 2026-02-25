package com.moondeveloper.sync

import kotlinx.coroutines.flow.StateFlow

/**
 * Network connectivity monitor.
 *
 * Platform implementations should observe the actual network state
 * (e.g., ConnectivityManager on Android, NWPathMonitor on iOS).
 *
 * @see NoOpNetworkMonitor for testing (always online)
 */
interface NetworkMonitor {
    /** Reactive stream of network connectivity. `true` if the device is online. */
    val isOnline: StateFlow<Boolean>
}
