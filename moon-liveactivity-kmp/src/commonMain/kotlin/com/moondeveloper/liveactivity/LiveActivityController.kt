package com.moondeveloper.liveactivity

/**
 * KMP Live Activity Controller interface.
 * Unifies Dynamic Island (iOS) / Now Bar (Samsung) / Ongoing Notification (AOSP).
 */
interface LiveActivityController {

    /** Check platform's Live Activity support */
    fun getCapability(): LiveActivityCapability

    /**
     * Start a Live Activity.
     * @param id Unique identifier (for multiple activities in same app)
     * @param data Display data
     */
    fun start(id: String, data: LiveActivityData)

    /**
     * Update an active Live Activity.
     * @param id Same id used in start()
     * @param data Updated data
     */
    fun update(id: String, data: LiveActivityData)

    /**
     * End a Live Activity.
     * @param id Same id used in start()
     */
    fun end(id: String)

    /** End all active Live Activities */
    fun endAll()

    /** Get current state for an activity */
    fun getState(id: String): LiveActivityState
}
