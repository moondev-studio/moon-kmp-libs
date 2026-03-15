package com.moondeveloper.sync

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RemoteStoreTest {

    private class FakeRemoteStore : RemoteStore {
        val docs = mutableMapOf<String, MutableMap<String, Map<String, Any?>>>()

        override suspend fun get(collection: String, documentId: String): Map<String, Any?>? =
            docs[collection]?.get(documentId)

        override suspend fun set(collection: String, documentId: String, data: Map<String, Any?>) {
            docs.getOrPut(collection) { mutableMapOf() }[documentId] = data
        }

        override suspend fun update(collection: String, documentId: String, data: Map<String, Any?>) {
            val existing = docs[collection]?.get(documentId) ?: return
            docs[collection]!![documentId] = existing + data
        }

        override suspend fun delete(collection: String, documentId: String) {
            docs[collection]?.remove(documentId)
        }

        override suspend fun query(collection: String, filters: List<QueryFilter>): List<Map<String, Any?>> {
            val all = docs[collection]?.values?.toList() ?: emptyList()
            return all.filter { doc ->
                filters.all { filter ->
                    when (filter.operator) {
                        FilterOperator.EQUAL -> doc[filter.field] == filter.value
                        FilterOperator.NOT_EQUAL -> doc[filter.field] != filter.value
                        FilterOperator.GREATER_THAN -> {
                            val v = doc[filter.field]
                            v is Comparable<*> && filter.value is Comparable<*> &&
                                @Suppress("UNCHECKED_CAST")
                                (v as Comparable<Any>) > (filter.value as Comparable<Any>)
                        }
                        FilterOperator.LESS_THAN -> {
                            val v = doc[filter.field]
                            v is Comparable<*> && filter.value is Comparable<*> &&
                                @Suppress("UNCHECKED_CAST")
                                (v as Comparable<Any>) < (filter.value as Comparable<Any>)
                        }
                        FilterOperator.ARRAY_CONTAINS -> {
                            val arr = doc[filter.field]
                            arr is List<*> && arr.contains(filter.value)
                        }
                        FilterOperator.IN -> {
                            val allowed = filter.value
                            allowed is List<*> && allowed.contains(doc[filter.field])
                        }
                    }
                }
            }
        }
    }

    @Test
    fun set_and_get_roundtrip() = runTest {
        val store = FakeRemoteStore()
        val data = mapOf<String, Any?>("name" to "test", "value" to 42)

        store.set("items", "id-1", data)
        val result = store.get("items", "id-1")

        assertEquals("test", result?.get("name"))
        assertEquals(42, result?.get("value"))
    }

    @Test
    fun get_nonexistent_returns_null() = runTest {
        val store = FakeRemoteStore()
        assertNull(store.get("items", "nonexistent"))
    }

    @Test
    fun update_merges_with_existing() = runTest {
        val store = FakeRemoteStore()
        store.set("items", "id-1", mapOf("name" to "old", "extra" to "keep"))
        store.update("items", "id-1", mapOf("name" to "new"))

        val result = store.get("items", "id-1")
        assertEquals("new", result?.get("name"))
        assertEquals("keep", result?.get("extra"))
    }

    @Test
    fun delete_removes_document() = runTest {
        val store = FakeRemoteStore()
        store.set("items", "id-1", mapOf("name" to "toDelete"))
        store.delete("items", "id-1")

        assertNull(store.get("items", "id-1"))
    }

    @Test
    fun query_with_equal_filter() = runTest {
        val store = FakeRemoteStore()
        store.set("users", "1", mapOf("role" to "admin", "name" to "Alice"))
        store.set("users", "2", mapOf("role" to "user", "name" to "Bob"))
        store.set("users", "3", mapOf("role" to "admin", "name" to "Charlie"))

        val admins = store.query("users", listOf(QueryFilter("role", FilterOperator.EQUAL, "admin")))

        assertEquals(2, admins.size)
        assertTrue(admins.all { it["role"] == "admin" })
    }

    @Test
    fun query_with_no_filters_returns_all() = runTest {
        val store = FakeRemoteStore()
        store.set("items", "1", mapOf("v" to 1))
        store.set("items", "2", mapOf("v" to 2))

        val result = store.query("items")
        assertEquals(2, result.size)
    }

    @Test
    fun query_empty_collection_returns_empty() = runTest {
        val store = FakeRemoteStore()
        val result = store.query("empty")
        assertTrue(result.isEmpty())
    }

    @Test
    fun queryFilter_data_class_equality() {
        val f1 = QueryFilter("status", FilterOperator.EQUAL, "active")
        val f2 = QueryFilter("status", FilterOperator.EQUAL, "active")
        assertEquals(f1, f2)
    }

    @Test
    fun filterOperator_has_all_variants() {
        val operators = FilterOperator.entries
        assertEquals(6, operators.size)
        assertTrue(operators.contains(FilterOperator.EQUAL))
        assertTrue(operators.contains(FilterOperator.NOT_EQUAL))
        assertTrue(operators.contains(FilterOperator.GREATER_THAN))
        assertTrue(operators.contains(FilterOperator.LESS_THAN))
        assertTrue(operators.contains(FilterOperator.ARRAY_CONTAINS))
        assertTrue(operators.contains(FilterOperator.IN))
    }
}
