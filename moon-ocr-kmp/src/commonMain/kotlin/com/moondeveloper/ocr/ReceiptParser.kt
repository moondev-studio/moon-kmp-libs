package com.moondeveloper.ocr

/**
 * Parses structured [ReceiptData] from raw [OcrResult].
 *
 * Implementations: rule-based (ReceiptTextParser) or AI-based (GeminiReceiptParsingService).
 */
interface ReceiptParser {
    suspend fun parse(ocrResult: OcrResult): ReceiptData
}
