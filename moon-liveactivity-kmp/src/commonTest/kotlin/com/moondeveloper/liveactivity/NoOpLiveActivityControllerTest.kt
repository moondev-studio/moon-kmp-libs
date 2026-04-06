package com.moondeveloper.liveactivity

import kotlin.test.Test
import kotlin.test.assertEquals

class NoOpLiveActivityControllerTest {

    private val controller = NoOpLiveActivityController()

    @Test
    fun getCapability_allFalse() {
        val capability = controller.getCapability()
        assertEquals(false, capability.isDynamicIslandSupported)
        assertEquals(false, capability.isNowBarSupported)
        assertEquals(false, capability.isOngoingNotificationSupported)
    }

    @Test
    fun getState_alwaysReturnsIdle() {
        controller.start("test", LiveActivityData(title = "Test", primaryValue = "0"))
        assertEquals(LiveActivityState.IDLE, controller.getState("test"))
    }

    @Test
    fun operationsDoNotThrow() {
        val data = LiveActivityData(title = "Test", primaryValue = "0")
        controller.start("test", data)
        controller.update("test", data)
        controller.end("test")
        controller.endAll()
    }
}
