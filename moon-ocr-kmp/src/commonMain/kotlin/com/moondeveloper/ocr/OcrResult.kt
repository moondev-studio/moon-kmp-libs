package com.moondeveloper.ocr

/**
 * OCR 인식 결과.
 *
 * @property fullText 인식된 전체 텍스트
 * @property blocks 텍스트 블록 리스트
 * @property confidence 전체 신뢰도 (0.0~1.0)
 */
data class OcrResult(
    val fullText: String,
    val blocks: List<TextBlock>,
    val confidence: Float = 0f
)
