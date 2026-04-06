package com.moondeveloper.health

import kotlin.time.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.hours

class HealthRecordTest {

    private val now = Clock.System.now()

    @Test
    fun healthRecord_createsWithRequiredFields() {
        val record = HealthRecord(
            id = "rec-1",
            type = HealthDataType.STEPS,
            startTime = now,
            endTime = now,
            value = 5000.0,
            unit = HealthUnit.COUNT
        )

        assertEquals("rec-1", record.id)
        assertEquals(HealthDataType.STEPS, record.type)
        assertEquals(5000.0, record.value)
        assertEquals(HealthUnit.COUNT, record.unit)
        assertEquals(emptyMap<String, Any>(), record.metadata)
        assertNull(record.source)
    }

    @Test
    fun healthRecord_createsWithOptionalFields() {
        val source = DataSource(
            packageName = "com.example.app",
            appName = "Example App",
            deviceId = "device-123",
            deviceType = DeviceType.WATCH
        )

        val record = HealthRecord(
            id = "rec-2",
            type = HealthDataType.HEART_RATE,
            startTime = now.minus(1.hours),
            endTime = now,
            value = 72.0,
            unit = HealthUnit.BPM,
            metadata = mapOf("zone" to "resting"),
            source = source
        )

        assertEquals("resting", record.metadata["zone"])
        assertEquals("com.example.app", record.source?.packageName)
        assertEquals(DeviceType.WATCH, record.source?.deviceType)
    }

    @Test
    fun dataSource_createsWithMinimalFields() {
        val source = DataSource(packageName = "com.example.app")

        assertEquals("com.example.app", source.packageName)
        assertNull(source.appName)
        assertNull(source.deviceId)
        assertNull(source.deviceType)
    }

    @Test
    fun healthDataType_containsExpectedTypes() {
        val allTypes = HealthDataType.entries

        assertTrue(HealthDataType.HEART_RATE in allTypes)
        assertTrue(HealthDataType.STEPS in allTypes)
        assertTrue(HealthDataType.SLEEP in allTypes)
        assertTrue(HealthDataType.WORKOUT in allTypes)
        assertTrue(HealthDataType.BLOOD_OXYGEN in allTypes)
    }

    @Test
    fun healthUnit_containsExpectedUnits() {
        val allUnits = HealthUnit.entries

        assertTrue(HealthUnit.BPM in allUnits)
        assertTrue(HealthUnit.COUNT in allUnits)
        assertTrue(HealthUnit.KCAL in allUnits)
        assertTrue(HealthUnit.PERCENT in allUnits)
        assertTrue(HealthUnit.METERS in allUnits)
    }

    @Test
    fun deviceType_containsExpectedTypes() {
        val allTypes = DeviceType.entries

        assertTrue(DeviceType.PHONE in allTypes)
        assertTrue(DeviceType.WATCH in allTypes)
        assertTrue(DeviceType.FITNESS_TRACKER in allTypes)
        assertTrue(DeviceType.SCALE in allTypes)
    }

    private fun assertTrue(condition: Boolean) {
        kotlin.test.assertTrue(condition)
    }
}
