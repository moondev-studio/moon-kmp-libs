package com.moondeveloper.health

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

/**
 * Platform-agnostic interface for reading and writing health data.
 *
 * Platform implementations should delegate to HealthConnect on Android
 * and HealthKit on iOS.
 *
 * @see NoOpHealthDataProvider for testing and unsupported platforms
 * @see FakeHealthDataProvider for unit testing with controlled data
 */
interface HealthDataProvider {

    /**
     * Check if the provider is available on this platform/device.
     */
    suspend fun isAvailable(): Boolean

    /**
     * Check if the app has permission to read the specified data types.
     *
     * @param types Data types to check
     * @return Map of data type to permission status
     */
    suspend fun checkPermissions(types: Set<HealthDataType>): Map<HealthDataType, PermissionStatus>

    /**
     * Request permission to read and write the specified data types.
     *
     * @param types Data types to request access for
     * @return Result indicating success or failure with reason
     */
    suspend fun requestPermissions(types: Set<HealthDataType>): PermissionResult

    /**
     * Read health records of the specified type within a time range.
     *
     * @param type The type of health data to read
     * @param startTime Start of the time range (inclusive)
     * @param endTime End of the time range (exclusive)
     * @return List of matching health records
     */
    suspend fun readRecords(
        type: HealthDataType,
        startTime: Instant,
        endTime: Instant
    ): ReadResult

    /**
     * Write a health record.
     *
     * @param record The record to write
     * @return Result indicating success or failure
     */
    suspend fun writeRecord(record: HealthRecord): WriteResult

    /**
     * Delete a health record by ID.
     *
     * @param id The record ID to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteRecord(id: String): DeleteResult

    /**
     * Observe changes to health data of the specified types.
     *
     * @param types Data types to observe
     * @return Flow emitting change events
     */
    fun observeChanges(types: Set<HealthDataType>): Flow<HealthDataChange>

    /**
     * Get aggregated data for a time range.
     *
     * @param type The type of health data to aggregate
     * @param startTime Start of the time range
     * @param endTime End of the time range
     * @param aggregationType How to aggregate the data
     * @return Aggregated result
     */
    suspend fun aggregate(
        type: HealthDataType,
        startTime: Instant,
        endTime: Instant,
        aggregationType: AggregationType
    ): AggregateResult
}

/**
 * Permission status for a health data type.
 */
enum class PermissionStatus {
    /** Permission granted */
    GRANTED,

    /** Permission denied */
    DENIED,

    /** Permission not determined (can request) */
    NOT_DETERMINED,

    /** Permission cannot be requested (restricted by policy) */
    RESTRICTED
}

/**
 * Result of a permission request.
 */
sealed class PermissionResult {
    data object Success : PermissionResult()
    data class Denied(val deniedTypes: Set<HealthDataType>) : PermissionResult()
    data class Error(val message: String) : PermissionResult()
}

/**
 * Result of a read operation.
 */
sealed class ReadResult {
    data class Success(val records: List<HealthRecord>) : ReadResult()
    data class Error(val code: ErrorCode, val message: String) : ReadResult()
}

/**
 * Result of a write operation.
 */
sealed class WriteResult {
    data class Success(val id: String) : WriteResult()
    data class Error(val code: ErrorCode, val message: String) : WriteResult()
}

/**
 * Result of a delete operation.
 */
sealed class DeleteResult {
    data object Success : DeleteResult()
    data class Error(val code: ErrorCode, val message: String) : DeleteResult()
}

/**
 * Health data change event.
 */
data class HealthDataChange(
    val type: HealthDataType,
    val changeType: ChangeType,
    val recordIds: List<String>
)

/**
 * Type of change to health data.
 */
enum class ChangeType {
    INSERTED,
    UPDATED,
    DELETED
}

/**
 * Type of aggregation operation.
 */
enum class AggregationType {
    SUM,
    AVERAGE,
    MIN,
    MAX,
    COUNT
}

/**
 * Result of an aggregation operation.
 */
sealed class AggregateResult {
    data class Success(val value: Double, val unit: HealthUnit) : AggregateResult()
    data class Error(val code: ErrorCode, val message: String) : AggregateResult()
}

/**
 * Error codes for health data operations.
 */
enum class ErrorCode {
    /** Permission not granted */
    PERMISSION_DENIED,

    /** Data type not supported on this platform */
    UNSUPPORTED_TYPE,

    /** No data found */
    NOT_FOUND,

    /** Health service not available */
    SERVICE_UNAVAILABLE,

    /** Invalid parameter */
    INVALID_PARAMETER,

    /** Rate limit exceeded */
    RATE_LIMITED,

    /** Unknown error */
    UNKNOWN
}
