package com.moondeveloper.sync

interface RemoteStore {
    suspend fun get(collection: String, documentId: String): Map<String, Any?>?
    suspend fun set(collection: String, documentId: String, data: Map<String, Any?>)
    suspend fun update(collection: String, documentId: String, data: Map<String, Any?>)
    suspend fun delete(collection: String, documentId: String)
    suspend fun query(collection: String, filters: List<QueryFilter> = emptyList()): List<Map<String, Any?>>
}

data class QueryFilter(
    val field: String,
    val operator: FilterOperator,
    val value: Any?
)

enum class FilterOperator {
    EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN, ARRAY_CONTAINS, IN
}
