package com.moondeveloper.ocr

/**
 * OCR engine interface for text recognition from images.
 *
 * Platform implementations: Android (ML Kit), iOS (Vision framework).
 *
 * @see NoOpOcrEngine for unsupported platforms
 */
interface OcrEngine {
    /**
     * Recognize text from an image byte array.
     *
     * @param image Raw image bytes (JPEG, PNG)
     * @return OCR result with full text, text blocks, and confidence score
     */
    suspend fun recognize(image: ByteArray): OcrResult

    /** Check if OCR is available on the current platform. */
    fun isAvailable(): Boolean
}
