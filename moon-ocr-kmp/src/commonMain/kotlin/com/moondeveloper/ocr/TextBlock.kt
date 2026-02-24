package com.moondeveloper.ocr

/**
 * OCR로 인식된 텍스트 블록.
 *
 * @property text 블록 내 텍스트
 * @property boundingBox 블록의 경계 사각형 (null이면 위치 정보 없음)
 */
data class TextBlock(
    val text: String,
    val boundingBox: BoundingBox? = null
)

/**
 * 텍스트 블록의 경계 사각형.
 */
data class BoundingBox(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)
