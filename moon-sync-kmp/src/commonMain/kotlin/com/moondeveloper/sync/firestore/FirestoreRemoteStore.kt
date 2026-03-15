package com.moondeveloper.sync.firestore

import com.moondeveloper.sync.FilterOperator
import com.moondeveloper.sync.QueryFilter
import com.moondeveloper.sync.RemoteStore
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Query
import dev.gitlive.firebase.firestore.WhereConstraint
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where

/**
 * Firestore-backed [RemoteStore] using GitLive KMP Firebase SDK.
 *
 * Applies [QueryFilter]s as Firestore where-clauses during [query] operations.
 */
class FirestoreRemoteStore : RemoteStore {
    private val firestore get() = Firebase.firestore

    override suspend fun get(collection: String, documentId: String): Map<String, Any?>? {
        return try {
            val doc = firestore.collection(collection).document(documentId).get()
            if (doc.exists) doc.data() else null
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun set(collection: String, documentId: String, data: Map<String, Any?>) {
        firestore.collection(collection).document(documentId).set(data)
    }

    override suspend fun update(collection: String, documentId: String, data: Map<String, Any?>) {
        firestore.collection(collection).document(documentId).update(data)
    }

    override suspend fun delete(collection: String, documentId: String) {
        firestore.collection(collection).document(documentId).delete()
    }

    /**
     * Query documents with filter support.
     * Converts [QueryFilter]s to Firestore where-clauses.
     */
    override suspend fun query(collection: String, filters: List<QueryFilter>): List<Map<String, Any?>> {
        return try {
            var query: Query = firestore.collection(collection)

            for (filter in filters) {
                query = applyFilter(query, filter)
            }

            val snapshot = query.get()
            snapshot.documents.mapNotNull { doc ->
                try {
                    doc.data<Map<String, Any?>>()
                } catch (_: Exception) {
                    null
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun applyFilter(query: Query, filter: QueryFilter): Query {
        val field = filter.field
        val value = filter.value ?: return query // Skip null-valued filters
        return when (filter.operator) {
            FilterOperator.EQUAL -> query.where { field equalTo value }
            FilterOperator.NOT_EQUAL -> query.where { field notEqualTo value }
            FilterOperator.GREATER_THAN -> query.where { field greaterThan value }
            FilterOperator.LESS_THAN -> query.where { field lessThan value }
            FilterOperator.ARRAY_CONTAINS -> query.where { field contains value }
            FilterOperator.IN -> {
                @Suppress("UNCHECKED_CAST")
                val list = value as? List<Any> ?: listOf(value)
                query.where { field inArray list }
            }
        }
    }
}
