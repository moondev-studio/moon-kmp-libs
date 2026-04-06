package com.moondeveloper.liveactivity

/**
 * In-memory fake [LiveActivityController] for unit testing.
 */
class FakeLiveActivityController : LiveActivityController {

    private val activities = mutableMapOf<String, LiveActivityData>()
    private val states = mutableMapOf<String, LiveActivityState>()

    val startedIds = mutableListOf<String>()
    val updatedIds = mutableListOf<String>()
    val endedIds = mutableListOf<String>()

    override fun getCapability() = LiveActivityCapability(
        isDynamicIslandSupported = false,
        isNowBarSupported = false,
        isOngoingNotificationSupported = true
    )

    override fun start(id: String, data: LiveActivityData) {
        activities[id] = data
        states[id] = LiveActivityState.ACTIVE
        startedIds.add(id)
    }

    override fun update(id: String, data: LiveActivityData) {
        if (states[id] != LiveActivityState.ACTIVE) return
        activities[id] = data
        updatedIds.add(id)
    }

    override fun end(id: String) {
        states[id] = LiveActivityState.ENDED
        endedIds.add(id)
    }

    override fun endAll() {
        activities.keys.toList().forEach { end(it) }
    }

    override fun getState(id: String): LiveActivityState {
        return states[id] ?: LiveActivityState.IDLE
    }

    fun getData(id: String): LiveActivityData? = activities[id]
}
