package com.moondeveloper.health

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.datetime.Instant

/**
 * No-op [HealthDataProvider] for testing and unsupported platforms.
 *
 * All read operations return empty results, all write operations succeed.
 */
object NoOpHealthDataProvider : HealthDataProvider {

    override suspend fun isAvailable(): Boolean = false

    override suspend fun checkPermissions(types: Set<HealthDataType>): Map<HealthDataType, PermissionStatus> =
        types.associateWith { PermissionStatus.NOT_DETERMINED }

    override suspend fun requestPermissions(types: Set<HealthDataType>): PermissionResult =
        PermissionResult.Success

    override suspend fun readRecords(
        type: HealthDataType,
        startTime: Instant,
        endTime: Instant
    ): ReadResult = ReadResult.Success(emptyList())

    override suspend fun writeRecord(record: HealthRecord): WriteResult =
        WriteResult.Success(record.id)

    override suspend fun deleteRecord(id: String): DeleteResult =
        DeleteResult.Success

    override fun observeChanges(types: Set<HealthDataType>): Flow<HealthDataChange> =
        emptyFlow()

    override suspend fun aggregate(
        type: HealthDataType,
        startTime: Instant,
        endTime: Instant,
        aggregationType: AggregationType
    ): AggregateResult = AggregateResult.Success(0.0, HealthUnit.NONE)
}
