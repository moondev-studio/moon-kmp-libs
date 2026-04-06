package com.moondeveloper.liveactivity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LiveActivityControllerTest {

    private val controller = FakeLiveActivityController()

    @Test
    fun start_stateBecomesActive() {
        val data = LiveActivityData(title = "Walking", primaryValue = "00:00")
        controller.start("walk_1", data)
        assertEquals(LiveActivityState.ACTIVE, controller.getState("walk_1"))
    }

    @Test
    fun start_recordsStartedId() {
        val data = LiveActivityData(title = "Walking", primaryValue = "00:00")
        controller.start("walk_1", data)
        assertTrue("walk_1" in controller.startedIds)
    }

    @Test
    fun end_stateBecomesEnded() {
        val data = LiveActivityData(title = "Walking", primaryValue = "00:00")
        controller.start("walk_1", data)
        controller.end("walk_1")
        assertEquals(LiveActivityState.ENDED, controller.getState("walk_1"))
    }

    @Test
    fun end_recordsEndedId() {
        val data = LiveActivityData(title = "Walking", primaryValue = "00:00")
        controller.start("walk_1", data)
        controller.end("walk_1")
        assertTrue("walk_1" in controller.endedIds)
    }

    @Test
    fun update_recordsUpdatedId() {
        val data = LiveActivityData(title = "Walking", primaryValue = "00:00")
        controller.start("walk_1", data)
        controller.update("walk_1", data.copy(primaryValue = "05:30"))
        assertTrue("walk_1" in controller.updatedIds)
    }

    @Test
    fun update_storesNewData() {
        val data = LiveActivityData(title = "Walking", primaryValue = "00:00")
        controller.start("walk_1", data)
        controller.update("walk_1", data.copy(primaryValue = "05:30"))
        assertEquals("05:30", controller.getData("walk_1")?.primaryValue)
    }

    @Test
    fun update_ignoredWhenNotActive() {
        val data = LiveActivityData(title = "Walking", primaryValue = "00:00")
        controller.update("nonexistent", data)
        assertTrue("nonexistent" !in controller.updatedIds)
    }

    @Test
    fun endAll_endsAllActiveSessions() {
        controller.start("walk_1", LiveActivityData(title = "A", primaryValue = "1"))
        controller.start("walk_2", LiveActivityData(title = "B", primaryValue = "2"))
        controller.endAll()
        assertEquals(LiveActivityState.ENDED, controller.getState("walk_1"))
        assertEquals(LiveActivityState.ENDED, controller.getState("walk_2"))
    }

    @Test
    fun getState_unknownIdReturnsIdle() {
        assertEquals(LiveActivityState.IDLE, controller.getState("unknown"))
    }

    @Test
    fun getCapability_returnsExpectedValues() {
        val capability = controller.getCapability()
        assertEquals(false, capability.isDynamicIslandSupported)
        assertEquals(false, capability.isNowBarSupported)
        assertEquals(true, capability.isOngoingNotificationSupported)
    }
}
