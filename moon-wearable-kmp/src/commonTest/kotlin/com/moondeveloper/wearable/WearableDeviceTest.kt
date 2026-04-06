package com.moondeveloper.wearable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WearableDeviceTest {

    @Test
    fun wearableDevice_createsWithRequiredFields() {
        val device = WearableDevice(
            id = "device-123",
            name = "My Watch",
            type = WearableType.WATCH
        )

        assertEquals("device-123", device.id)
        assertEquals("My Watch", device.name)
        assertEquals(WearableType.WATCH, device.type)
        assertNull(device.manufacturer)
        assertNull(device.model)
        assertTrue(device.platformInfo.isEmpty())
    }

    @Test
    fun wearableDevice_createsWithAllFields() {
        val device = WearableDevice(
            id = "device-456",
            name = "Galaxy Watch",
            type = WearableType.WATCH,
            manufacturer = "Samsung",
            model = "Watch 6",
            platformInfo = mapOf("os" to "Wear OS 4", "api" to "33")
        )

        assertEquals("Samsung", device.manufacturer)
        assertEquals("Watch 6", device.model)
        assertEquals("Wear OS 4", device.platformInfo["os"])
    }

    @Test
    fun wearableType_containsExpectedTypes() {
        val allTypes = WearableType.entries

        assertTrue(WearableType.WATCH in allTypes)
        assertTrue(WearableType.FITNESS_BAND in allTypes)
        assertTrue(WearableType.RING in allTypes)
        assertTrue(WearableType.EARBUDS in allTypes)
        assertTrue(WearableType.GLASSES in allTypes)
    }

    @Test
    fun wearableCapability_containsExpectedCapabilities() {
        val allCaps = WearableCapability.entries

        assertTrue(WearableCapability.HEART_RATE in allCaps)
        assertTrue(WearableCapability.STEPS in allCaps)
        assertTrue(WearableCapability.GPS in allCaps)
        assertTrue(WearableCapability.SEND_MESSAGES in allCaps)
        assertTrue(WearableCapability.RECEIVE_MESSAGES in allCaps)
        assertTrue(WearableCapability.DATA_SYNC in allCaps)
    }
}
