package com.moondeveloper.sync

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ConflictResolverTest {

    @Test
    fun serverWins_returns_useRemote() = runTest {
        val resolver = ServerWinsConflictResolver()
        val local = mapOf<String, Any?>("id" to "1", "value" to "local")
        val remote = mapOf<String, Any?>("id" to "1", "value" to "remote")

        val result = resolver.resolve(local, remote)

        assertIs<ConflictResolution.UseRemote>(result)
        assertEquals(remote, result.data)
    }

    @Test
    fun serverWins_ignores_base() = runTest {
        val resolver = ServerWinsConflictResolver()
        val local = mapOf<String, Any?>("v" to 1)
        val remote = mapOf<String, Any?>("v" to 2)
        val base = mapOf<String, Any?>("v" to 0)

        val result = resolver.resolve(local, remote, base)

        assertIs<ConflictResolution.UseRemote>(result)
        assertEquals(remote, result.data)
    }

    @Test
    fun serverWins_with_null_base() = runTest {
        val resolver = ServerWinsConflictResolver()
        val local = mapOf<String, Any?>("data" to "A")
        val remote = mapOf<String, Any?>("data" to "B")

        val result = resolver.resolve(local, remote, base = null)

        assertIs<ConflictResolution.UseRemote>(result)
    }

    @Test
    fun serverWins_preserves_remote_data_exactly() = runTest {
        val resolver = ServerWinsConflictResolver()
        val remote = mapOf<String, Any?>("key" to "value", "count" to 42, "nullable" to null)

        val result = resolver.resolve(emptyMap(), remote)

        assertIs<ConflictResolution.UseRemote>(result)
        assertEquals("value", result.data["key"])
        assertEquals(42, result.data["count"])
        assertEquals(null, result.data["nullable"])
    }

    @Test
    fun conflictResolution_useLocal_holds_data() {
        val data = mapOf<String, Any?>("x" to 1)
        val resolution = ConflictResolution.UseLocal(data)
        assertEquals(data, resolution.data)
    }

    @Test
    fun conflictResolution_merged_holds_data() {
        val merged = mapOf<String, Any?>("merged" to true)
        val resolution = ConflictResolution.Merged(merged)
        assertEquals(merged, resolution.data)
    }

    @Test
    fun conflictResolution_requireUserInput_holds_both_sides() {
        val local = mapOf<String, Any?>("side" to "local")
        val remote = mapOf<String, Any?>("side" to "remote")
        val resolution = ConflictResolution.RequireUserInput(local, remote)
        assertEquals(local, resolution.local)
        assertEquals(remote, resolution.remote)
    }
}
