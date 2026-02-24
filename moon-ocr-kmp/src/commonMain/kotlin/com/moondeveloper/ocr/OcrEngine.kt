package com.moondeveloper.ocr

/**
 * OCR 엔진 인터페이스.
 * 플랫폼별 구현체: Android(ML Kit), iOS(Vision).
 */
interface OcrEngine {
    /**
     * 이미지 바이트 배열에서 텍스트를 인식합니다.
     */
    suspend fun recognize(image: ByteArray): OcrResult

    /**
     * 현재 플랫폼에서 OCR이 사용 가능한지 확인합니다.
     */
    fun isAvailable(): Boolean
}
