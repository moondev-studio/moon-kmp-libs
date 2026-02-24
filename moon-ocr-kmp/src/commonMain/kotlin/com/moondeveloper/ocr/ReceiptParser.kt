package com.moondeveloper.ocr

/**
 * OCR 결과에서 영수증 데이터를 파싱하는 인터페이스.
 * 구현체: 규칙 기반(ReceiptTextParser) 또는 AI 기반(GeminiReceiptParsingService).
 */
interface ReceiptParser {
    suspend fun parse(ocrResult: OcrResult): ReceiptData
}
