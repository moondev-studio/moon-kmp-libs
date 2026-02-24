package com.moondeveloper.billing

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class NoOpBillingEngineTest {

    @Test
    fun availableProducts_emits_empty_list() = runTest {
        val products = NoOpBillingEngine.availableProducts.first()
        assertTrue(products.isEmpty())
    }

    @Test
    fun purchaseState_emits_idle() = runTest {
        val state = NoOpBillingEngine.purchaseState.first()
        assertIs<PurchaseState.Idle>(state)
    }

    @Test
    fun queryProducts_returns_empty() = runTest {
        val result = NoOpBillingEngine.queryProducts(listOf("prod_1"))
        assertTrue(result.isEmpty())
    }

    @Test
    fun purchase_returns_error() = runTest {
        val product = Product("id", "name", "desc", "$1", 1_000_000, "USD", ProductType.ONE_TIME)
        val result = NoOpBillingEngine.purchase(product)
        assertIs<PurchaseResult.Error>(result)
    }

    @Test
    fun restorePurchases_returns_error() = runTest {
        val result = NoOpBillingEngine.restorePurchases()
        assertIs<PurchaseResult.Error>(result)
    }

    @Test
    fun consumePurchase_returns_false() = runTest {
        assertFalse(NoOpBillingEngine.consumePurchase("token"))
    }
}
