package com.moondeveloper.ocr

/**
 * Parsed receipt data extracted from OCR result.
 *
 * All monetary values are in the smallest currency unit (e.g., cents).
 */
data class ReceiptData(
    val storeName: String? = null,
    val items: List<ReceiptItem> = emptyList(),
    val subtotal: Long? = null,
    val tax: Long? = null,
    val total: Long? = null,
    val currency: String? = null
)

/** A single line item on a receipt. */
data class ReceiptItem(
    val name: String,
    val quantity: Int = 1,
    val unitPrice: Long = 0,
    val totalPrice: Long = 0
)
