package com.moondeveloper.health

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.datetime.Instant

/**
 * Fake [HealthDataProvider] for unit testing with controlled data.
 *
 * Allows pre-populating records and verifying write/delete operations.
 */
class FakeHealthDataProvider : HealthDataProvider {

    private val records = mutableMapOf<String, HealthRecord>()
    private val permissions = mutableMapOf<HealthDataType, PermissionStatus>()
    private val changeFlow = MutableSharedFlow<HealthDataChange>()

    var isAvailableValue = true

    /** All written records (for verification) */
    val writtenRecords: List<HealthRecord> get() = records.values.toList()

    /** All deleted record IDs (for verification) */
    val deletedIds = mutableListOf<String>()

    /** Pre-populate records for testing */
    fun addRecords(vararg recordList: HealthRecord) {
        recordList.forEach { records[it.id] = it }
    }

    /** Set permission status for a data type */
    fun setPermission(type: HealthDataType, status: PermissionStatus) {
        permissions[type] = status
    }

    /** Clear all records */
    fun clear() {
        records.clear()
        deletedIds.clear()
        permissions.clear()
    }

    /** Emit a change event for testing observers */
    suspend fun emitChange(change: HealthDataChange) {
        changeFlow.emit(change)
    }

    override suspend fun isAvailable(): Boolean = isAvailableValue

    override suspend fun checkPermissions(types: Set<HealthDataType>): Map<HealthDataType, PermissionStatus> =
        types.associateWith { permissions[it] ?: PermissionStatus.NOT_DETERMINED }

    override suspend fun requestPermissions(types: Set<HealthDataType>): PermissionResult {
        val denied = types.filter { permissions[it] == PermissionStatus.DENIED }
        return if (denied.isEmpty()) {
            types.forEach { permissions[it] = PermissionStatus.GRANTED }
            PermissionResult.Success
        } else {
            PermissionResult.Denied(denied.toSet())
        }
    }

    override suspend fun readRecords(
        type: HealthDataType,
        startTime: Instant,
        endTime: Instant
    ): ReadResult {
        val matching = records.values
            .filter { it.type == type }
            .filter { it.startTime >= startTime && it.startTime < endTime }
            .sortedBy { it.startTime }
        return ReadResult.Success(matching)
    }

    override suspend fun writeRecord(record: HealthRecord): WriteResult {
        records[record.id] = record
        return WriteResult.Success(record.id)
    }

    override suspend fun deleteRecord(id: String): DeleteResult {
        return if (records.remove(id) != null) {
            deletedIds.add(id)
            DeleteResult.Success
        } else {
            DeleteResult.Error(ErrorCode.NOT_FOUND, "Record not found: $id")
        }
    }

    override fun observeChanges(types: Set<HealthDataType>): Flow<HealthDataChange> =
        changeFlow.filter { it.type in types }

    override suspend fun aggregate(
        type: HealthDataType,
        startTime: Instant,
        endTime: Instant,
        aggregationType: AggregationType
    ): AggregateResult {
        val matching = records.values
            .filter { it.type == type }
            .filter { it.startTime >= startTime && it.startTime < endTime }

        if (matching.isEmpty()) {
            return AggregateResult.Success(0.0, HealthUnit.NONE)
        }

        val unit = matching.first().unit
        val value = when (aggregationType) {
            AggregationType.SUM -> matching.sumOf { it.value }
            AggregationType.AVERAGE -> matching.map { it.value }.average()
            AggregationType.MIN -> matching.minOf { it.value }
            AggregationType.MAX -> matching.maxOf { it.value }
            AggregationType.COUNT -> matching.size.toDouble()
        }

        return AggregateResult.Success(value, unit)
    }
}
