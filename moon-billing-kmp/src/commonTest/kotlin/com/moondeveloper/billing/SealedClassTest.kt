package com.moondeveloper.billing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SealedClassTest {

    @Test
    fun purchaseResult_pattern_matching() {
        val results: List<PurchaseResult> = listOf(
            PurchaseResult.Success("prod_1", "token_1", "receipt_1"),
            PurchaseResult.Cancelled("prod_2"),
            PurchaseResult.Error(RuntimeException("fail")),
            PurchaseResult.AlreadyOwned("prod_3")
        )

        assertIs<PurchaseResult.Success>(results[0])
        assertIs<PurchaseResult.Cancelled>(results[1])
        assertIs<PurchaseResult.Error>(results[2])
        assertIs<PurchaseResult.AlreadyOwned>(results[3])

        val success = results[0] as PurchaseResult.Success
        assertEquals("prod_1", success.productId)
        assertEquals("token_1", success.purchaseToken)
    }

    @Test
    fun purchaseState_pattern_matching() {
        val states: List<PurchaseState> = listOf(
            PurchaseState.Idle,
            PurchaseState.Loading,
            PurchaseState.Purchased("prod_1"),
            PurchaseState.Error("something went wrong")
        )

        assertIs<PurchaseState.Idle>(states[0])
        assertIs<PurchaseState.Loading>(states[1])
        assertIs<PurchaseState.Purchased>(states[2])
        assertIs<PurchaseState.Error>(states[3])

        val purchased = states[2] as PurchaseState.Purchased
        assertEquals("prod_1", purchased.productId)
    }

    @Test
    fun limitCheckResult_pattern_matching() {
        val results: List<LimitCheckResult> = listOf(
            LimitCheckResult.Allowed,
            LimitCheckResult.LimitReached(limit = 5, used = 5),
            LimitCheckResult.PremiumRequired
        )

        assertIs<LimitCheckResult.Allowed>(results[0])
        assertIs<LimitCheckResult.LimitReached>(results[1])
        assertIs<LimitCheckResult.PremiumRequired>(results[2])

        val limitReached = results[1] as LimitCheckResult.LimitReached
        assertEquals(5, limitReached.limit)
        assertEquals(5, limitReached.used)
    }

    @Test
    fun product_equality_and_copy() {
        val product = Product(
            id = "prod_1",
            name = "Premium",
            description = "Premium plan",
            price = "$9.99",
            priceMicros = 9_990_000,
            currencyCode = "USD",
            type = ProductType.SUBSCRIPTION
        )

        val copy = product.copy(price = "$7.99", priceMicros = 7_990_000)
        assertEquals("prod_1", copy.id)
        assertEquals("$7.99", copy.price)
        assertEquals(7_990_000, copy.priceMicros)

        val same = product.copy()
        assertEquals(product, same)
    }

    @Test
    fun productType_values() {
        val types = ProductType.entries
        assertEquals(3, types.size)
        assertEquals(ProductType.ONE_TIME, types[0])
        assertEquals(ProductType.CONSUMABLE, types[1])
        assertEquals(ProductType.SUBSCRIPTION, types[2])
    }

    @Test
    fun premiumTier_values() {
        val tiers = PremiumTier.entries
        assertEquals(3, tiers.size)
        assertEquals(PremiumTier.FREE, tiers[0])
        assertEquals(PremiumTier.PREMIUM, tiers[1])
        assertEquals(PremiumTier.PREMIUM_PLUS, tiers[2])
    }
}
