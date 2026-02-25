package com.moondeveloper.ocr

/**
 * A block of text recognized by OCR.
 *
 * @property text The text content of this block
 * @property boundingBox Bounding rectangle, or `null` if position data is unavailable
 */
data class TextBlock(
    val text: String,
    val boundingBox: BoundingBox? = null
)

/** Bounding rectangle for a text block (pixel coordinates). */
data class BoundingBox(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)
