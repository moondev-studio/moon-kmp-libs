package com.moondeveloper.sync

/**
 * Abstraction for remote data storage (e.g., Firestore, REST API).
 *
 * Provides CRUD operations and query filtering on document collections.
 */
interface RemoteStore {
    suspend fun get(collection: String, documentId: String): Map<String, Any?>?
    suspend fun set(collection: String, documentId: String, data: Map<String, Any?>)
    suspend fun update(collection: String, documentId: String, data: Map<String, Any?>)
    suspend fun delete(collection: String, documentId: String)
    suspend fun query(collection: String, filters: List<QueryFilter> = emptyList()): List<Map<String, Any?>>
}

/** Query filter for [RemoteStore.query]. */
data class QueryFilter(
    val field: String,
    val operator: FilterOperator,
    val value: Any?
)

/** Comparison operators for [QueryFilter]. */
enum class FilterOperator {
    EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN, ARRAY_CONTAINS, IN
}
