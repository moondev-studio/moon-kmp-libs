package com.moondeveloper.ocr

/** No-op [OcrEngine] for unsupported platforms. Always returns empty results. */
class NoOpOcrEngine : OcrEngine {
    override suspend fun recognize(image: ByteArray): OcrResult =
        OcrResult(fullText = "", blocks = emptyList())

    override fun isAvailable(): Boolean = false
}
