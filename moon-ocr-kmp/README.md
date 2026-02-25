# moon-ocr-kmp

OCR and receipt parsing abstraction for Kotlin Multiplatform.

## Features

- **OCR engine** abstraction for text recognition from images
- **Receipt parser** interface for extracting structured receipt data
- **Text block** model with bounding box coordinates
- **Receipt data model** (store name, items, subtotal, tax, total)
- **NoOp implementation** for unsupported platforms

## Installation

```kotlin
// includeBuild (local development)
implementation("com.moondeveloper:moon-ocr-kmp")

// Maven Central (coming soon)
implementation("com.moondeveloper:moon-ocr-kmp:1.0.0")
```

## Quick Start

```kotlin
val ocrEngine: OcrEngine = get()
val receiptParser: ReceiptParser = get()

// Check availability
if (!ocrEngine.isAvailable()) {
    showError("OCR not available on this platform")
    return
}

// Recognize text from image
val imageBytes: ByteArray = loadImage()
val ocrResult: OcrResult = ocrEngine.recognize(imageBytes)
println(ocrResult.fullText)
println("Confidence: ${ocrResult.confidence}")

// Parse receipt from OCR result
val receipt: ReceiptData = receiptParser.parse(ocrResult)
println("Store: ${receipt.storeName}")
receipt.items.forEach { item ->
    println("${item.name}: ${item.quantity} x ${item.unitPrice} = ${item.totalPrice}")
}
println("Total: ${receipt.total}")
```

## API Overview

| Type | Description |
|------|-------------|
| `OcrEngine` | Core OCR interface (recognize, isAvailable) |
| `OcrResult` | Recognition result (fullText, blocks, confidence) |
| `TextBlock` | Recognized text block with optional bounding box |
| `BoundingBox` | Rectangle coordinates (left, top, right, bottom) |
| `ReceiptParser` | Parse OcrResult into structured ReceiptData |
| `ReceiptData` | Parsed receipt (storeName, items, subtotal, tax, total, currency) |
| `ReceiptItem` | Receipt line item (name, quantity, unitPrice, totalPrice) |
| `NoOpOcrEngine` | Returns empty result, isAvailable() = false |

## Platform Support

| Platform | Status |
|----------|--------|
| Android | Supported |
| iOS | Supported |
| Desktop (JVM) | Supported (NoOp) |

## License

Apache License 2.0
