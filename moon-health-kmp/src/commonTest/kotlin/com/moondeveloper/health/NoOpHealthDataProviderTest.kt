package com.moondeveloper.health

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.time.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours

class NoOpHealthDataProviderTest {

    private val now = Clock.System.now()

    @Test
    fun isAvailable_returnsFalse() = runTest {
        assertFalse(NoOpHealthDataProvider.isAvailable())
    }

    @Test
    fun checkPermissions_returnsNotDetermined() = runTest {
        val types = setOf(HealthDataType.STEPS, HealthDataType.HEART_RATE)
        val result = NoOpHealthDataProvider.checkPermissions(types)

        types.forEach { type ->
            assertEquals(PermissionStatus.NOT_DETERMINED, result[type])
        }
    }

    @Test
    fun requestPermissions_returnsSuccess() = runTest {
        val result = NoOpHealthDataProvider.requestPermissions(setOf(HealthDataType.STEPS))
        assertEquals(PermissionResult.Success, result)
    }

    @Test
    fun readRecords_returnsEmptyList() = runTest {
        val result = NoOpHealthDataProvider.readRecords(
            HealthDataType.STEPS,
            now.minus(1.hours),
            now
        )

        assertTrue(result is ReadResult.Success)
        assertTrue((result as ReadResult.Success).records.isEmpty())
    }

    @Test
    fun writeRecord_returnsSuccessWithId() = runTest {
        val record = HealthRecord(
            id = "test-id",
            type = HealthDataType.STEPS,
            startTime = now,
            endTime = now,
            value = 1000.0,
            unit = HealthUnit.COUNT
        )

        val result = NoOpHealthDataProvider.writeRecord(record)

        assertTrue(result is WriteResult.Success)
        assertEquals("test-id", (result as WriteResult.Success).id)
    }

    @Test
    fun deleteRecord_returnsSuccess() = runTest {
        val result = NoOpHealthDataProvider.deleteRecord("any-id")
        assertEquals(DeleteResult.Success, result)
    }

    @Test
    fun observeChanges_emitsNothing() = runTest {
        val changes = NoOpHealthDataProvider.observeChanges(setOf(HealthDataType.STEPS))
            .toList()

        assertTrue(changes.isEmpty())
    }

    @Test
    fun aggregate_returnsZero() = runTest {
        val result = NoOpHealthDataProvider.aggregate(
            HealthDataType.STEPS,
            now.minus(1.hours),
            now,
            AggregationType.SUM
        )

        assertTrue(result is AggregateResult.Success)
        assertEquals(0.0, (result as AggregateResult.Success).value)
        assertEquals(HealthUnit.NONE, result.unit)
    }
}
