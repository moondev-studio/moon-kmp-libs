package com.moondeveloper.ocr

/**
 * OCR recognition result.
 *
 * @property fullText The complete recognized text
 * @property blocks List of text blocks with positional data
 * @property confidence Overall recognition confidence (0.0-1.0)
 */
data class OcrResult(
    val fullText: String,
    val blocks: List<TextBlock>,
    val confidence: Float = 0f
)
