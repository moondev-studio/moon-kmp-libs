package com.moondeveloper.analytics

/**
 * Screen view tracking for navigation analytics.
 *
 * @see NoOpScreenTracker for testing
 */
interface ScreenTracker {
    /**
     * Track a screen view event.
     *
     * @param screenName Display name of the screen (e.g., "HomeScreen")
     * @param screenClass Optional fully-qualified class name
     */
    fun trackScreenView(screenName: String, screenClass: String? = null)
}
