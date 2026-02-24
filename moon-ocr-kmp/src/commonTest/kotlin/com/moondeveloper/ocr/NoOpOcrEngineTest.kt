package com.moondeveloper.ocr

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NoOpOcrEngineTest {

    private val engine = NoOpOcrEngine()

    @Test
    fun isAvailable_returnsFalse() {
        assertFalse(engine.isAvailable())
    }

    @Test
    fun recognize_returnsEmptyResult() = runTest {
        val result = engine.recognize(byteArrayOf(1, 2, 3))
        assertEquals("", result.fullText)
        assertTrue(result.blocks.isEmpty())
        assertEquals(0f, result.confidence)
    }
}
