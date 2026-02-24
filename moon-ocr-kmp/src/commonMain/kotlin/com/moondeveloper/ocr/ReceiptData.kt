package com.moondeveloper.ocr

/**
 * 파싱된 영수증 데이터.
 */
data class ReceiptData(
    val storeName: String? = null,
    val items: List<ReceiptItem> = emptyList(),
    val subtotal: Long? = null,
    val tax: Long? = null,
    val total: Long? = null,
    val currency: String? = null
)

/**
 * 영수증 항목.
 */
data class ReceiptItem(
    val name: String,
    val quantity: Int = 1,
    val unitPrice: Long = 0,
    val totalPrice: Long = 0
)
