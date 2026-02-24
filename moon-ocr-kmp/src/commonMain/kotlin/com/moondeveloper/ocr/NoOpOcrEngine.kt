package com.moondeveloper.ocr

/**
 * OCR 미지원 플랫폼용 NoOp 구현.
 */
class NoOpOcrEngine : OcrEngine {
    override suspend fun recognize(image: ByteArray): OcrResult =
        OcrResult(fullText = "", blocks = emptyList())

    override fun isAvailable(): Boolean = false
}
