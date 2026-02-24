package com.moondeveloper.billing

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: String,
    val priceMicros: Long,
    val currencyCode: String,
    val type: ProductType
)

enum class ProductType {
    ONE_TIME,
    CONSUMABLE,
    SUBSCRIPTION
}
