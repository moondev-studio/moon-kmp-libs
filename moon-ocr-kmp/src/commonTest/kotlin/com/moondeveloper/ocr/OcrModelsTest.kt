package com.moondeveloper.ocr

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OcrModelsTest {

    @Test
    fun ocrResult_defaultConfidence() {
        val result = OcrResult(fullText = "test", blocks = emptyList())
        assertEquals(0f, result.confidence)
    }

    @Test
    fun textBlock_nullBoundingBox() {
        val block = TextBlock(text = "hello")
        assertNull(block.boundingBox)
    }

    @Test
    fun textBlock_withBoundingBox() {
        val box = BoundingBox(left = 10, top = 20, right = 100, bottom = 50)
        val block = TextBlock(text = "hello", boundingBox = box)
        assertEquals(10, block.boundingBox?.left)
        assertEquals(50, block.boundingBox?.bottom)
    }

    @Test
    fun receiptData_defaults() {
        val data = ReceiptData()
        assertNull(data.storeName)
        assertEquals(emptyList(), data.items)
        assertNull(data.subtotal)
        assertNull(data.tax)
        assertNull(data.total)
        assertNull(data.currency)
    }

    @Test
    fun receiptItem_defaults() {
        val item = ReceiptItem(name = "Coffee")
        assertEquals(1, item.quantity)
        assertEquals(0L, item.unitPrice)
        assertEquals(0L, item.totalPrice)
    }

    @Test
    fun receiptData_withItems() {
        val items = listOf(
            ReceiptItem(name = "Coffee", quantity = 2, unitPrice = 4500, totalPrice = 9000),
            ReceiptItem(name = "Cake", quantity = 1, unitPrice = 6000, totalPrice = 6000)
        )
        val data = ReceiptData(
            storeName = "Cafe",
            items = items,
            subtotal = 15000,
            tax = 1500,
            total = 16500,
            currency = "KRW"
        )
        assertEquals(2, data.items.size)
        assertEquals(16500L, data.total)
    }
}
