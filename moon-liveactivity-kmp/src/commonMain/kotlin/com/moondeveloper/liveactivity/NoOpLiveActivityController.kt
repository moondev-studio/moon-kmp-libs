package com.moondeveloper.liveactivity

/**
 * No-op [LiveActivityController] for platforms without Live Activity support.
 */
class NoOpLiveActivityController : LiveActivityController {

    override fun getCapability() = LiveActivityCapability(
        isDynamicIslandSupported = false,
        isNowBarSupported = false,
        isOngoingNotificationSupported = false
    )

    override fun start(id: String, data: LiveActivityData) = Unit

    override fun update(id: String, data: LiveActivityData) = Unit

    override fun end(id: String) = Unit

    override fun endAll() = Unit

    override fun getState(id: String): LiveActivityState = LiveActivityState.IDLE
}
