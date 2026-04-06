package com.moondeveloper.health

import kotlinx.coroutines.test.runTest
import kotlin.time.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours

class FakeHealthDataProviderTest {

    private val now = Clock.System.now()
    private val provider = FakeHealthDataProvider()

    @Test
    fun isAvailable_returnsConfiguredValue() = runTest {
        provider.isAvailableValue = true
        assertTrue(provider.isAvailable())

        provider.isAvailableValue = false
        assertEquals(false, provider.isAvailable())
    }

    @Test
    fun checkPermissions_returnsNotDeterminedByDefault() = runTest {
        val types = setOf(HealthDataType.STEPS, HealthDataType.HEART_RATE)
        val result = provider.checkPermissions(types)

        assertEquals(PermissionStatus.NOT_DETERMINED, result[HealthDataType.STEPS])
        assertEquals(PermissionStatus.NOT_DETERMINED, result[HealthDataType.HEART_RATE])
    }

    @Test
    fun requestPermissions_grantsPermissionsWhenNotDenied() = runTest {
        val types = setOf(HealthDataType.STEPS)
        val result = provider.requestPermissions(types)

        assertEquals(PermissionResult.Success, result)

        val check = provider.checkPermissions(types)
        assertEquals(PermissionStatus.GRANTED, check[HealthDataType.STEPS])
    }

    @Test
    fun requestPermissions_returnsDeniedForPreSetDenied() = runTest {
        provider.setPermission(HealthDataType.STEPS, PermissionStatus.DENIED)

        val result = provider.requestPermissions(setOf(HealthDataType.STEPS))

        assertTrue(result is PermissionResult.Denied)
        assertTrue(HealthDataType.STEPS in (result as PermissionResult.Denied).deniedTypes)
    }

    @Test
    fun readRecords_returnsMatchingRecordsInTimeRange() = runTest {
        val record1 = createRecord("1", HealthDataType.STEPS, now.minus(2.hours), 1000.0)
        val record2 = createRecord("2", HealthDataType.STEPS, now.minus(1.hours), 500.0)
        val record3 = createRecord("3", HealthDataType.HEART_RATE, now.minus(1.hours), 72.0)

        provider.addRecords(record1, record2, record3)

        val result = provider.readRecords(
            HealthDataType.STEPS,
            now.minus(3.hours),
            now
        )

        assertTrue(result is ReadResult.Success)
        assertEquals(2, (result as ReadResult.Success).records.size)
    }

    @Test
    fun writeRecord_addsToWrittenRecords() = runTest {
        val record = createRecord("test-1", HealthDataType.STEPS, now, 1000.0)

        val result = provider.writeRecord(record)

        assertTrue(result is WriteResult.Success)
        assertEquals("test-1", (result as WriteResult.Success).id)
        assertEquals(1, provider.writtenRecords.size)
    }

    @Test
    fun deleteRecord_removesRecordAndTracksId() = runTest {
        val record = createRecord("del-1", HealthDataType.STEPS, now, 1000.0)
        provider.addRecords(record)

        val result = provider.deleteRecord("del-1")

        assertEquals(DeleteResult.Success, result)
        assertTrue("del-1" in provider.deletedIds)
        assertTrue(provider.writtenRecords.none { it.id == "del-1" })
    }

    @Test
    fun deleteRecord_returnsErrorForNonexistent() = runTest {
        val result = provider.deleteRecord("nonexistent")

        assertTrue(result is DeleteResult.Error)
        assertEquals(ErrorCode.NOT_FOUND, (result as DeleteResult.Error).code)
    }

    @Test
    fun aggregate_sumCalculatesCorrectly() = runTest {
        provider.addRecords(
            createRecord("1", HealthDataType.STEPS, now.minus(2.hours), 1000.0),
            createRecord("2", HealthDataType.STEPS, now.minus(1.hours), 500.0)
        )

        val result = provider.aggregate(
            HealthDataType.STEPS,
            now.minus(3.hours),
            now,
            AggregationType.SUM
        )

        assertTrue(result is AggregateResult.Success)
        assertEquals(1500.0, (result as AggregateResult.Success).value)
    }

    @Test
    fun aggregate_averageCalculatesCorrectly() = runTest {
        provider.addRecords(
            createRecord("1", HealthDataType.HEART_RATE, now.minus(2.hours), 70.0, HealthUnit.BPM),
            createRecord("2", HealthDataType.HEART_RATE, now.minus(1.hours), 80.0, HealthUnit.BPM)
        )

        val result = provider.aggregate(
            HealthDataType.HEART_RATE,
            now.minus(3.hours),
            now,
            AggregationType.AVERAGE
        )

        assertTrue(result is AggregateResult.Success)
        assertEquals(75.0, (result as AggregateResult.Success).value)
    }

    @Test
    fun clear_removesAllData() = runTest {
        provider.addRecords(createRecord("1", HealthDataType.STEPS, now, 1000.0))
        provider.setPermission(HealthDataType.STEPS, PermissionStatus.GRANTED)

        provider.clear()

        assertTrue(provider.writtenRecords.isEmpty())
        assertEquals(PermissionStatus.NOT_DETERMINED, provider.checkPermissions(setOf(HealthDataType.STEPS))[HealthDataType.STEPS])
    }

    private fun createRecord(
        id: String,
        type: HealthDataType,
        time: kotlin.time.Instant,
        value: Double,
        unit: HealthUnit = HealthUnit.COUNT
    ) = HealthRecord(
        id = id,
        type = type,
        startTime = time,
        endTime = time,
        value = value,
        unit = unit
    )
}
