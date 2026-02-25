package com.moondeveloper.billing

/**
 * In-app product data.
 *
 * @property id Store product identifier
 * @property name Display name
 * @property description Product description
 * @property price Formatted price string (e.g., "$4.99")
 * @property priceMicros Price in micros (e.g., 4990000 for $4.99)
 * @property currencyCode ISO 4217 currency code
 * @property type Product type (one-time, consumable, subscription)
 */
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: String,
    val priceMicros: Long,
    val currencyCode: String,
    val type: ProductType
)

/** Type of in-app product. */
enum class ProductType {
    ONE_TIME,
    CONSUMABLE,
    SUBSCRIPTION
}
