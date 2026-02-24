package com.moondeveloper.billing

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeBillingEngineTest {

    private val testProduct = Product(
        id = "premium_monthly",
        name = "Premium Monthly",
        description = "Monthly premium subscription",
        price = "$4.99",
        priceMicros = 4_990_000,
        currencyCode = "USD",
        type = ProductType.SUBSCRIPTION
    )

    @Test
    fun initialize_sets_flag() = runTest {
        val engine = FakeBillingEngine()
        assertFalse(engine.initialized)
        engine.initialize()
        assertTrue(engine.initialized)
    }

    @Test
    fun purchase_success_updates_state_and_returns_token() = runTest {
        val engine = FakeBillingEngine()
        val result = engine.purchase(testProduct)

        assertIs<PurchaseResult.Success>(result)
        assertEquals("premium_monthly", result.productId)
        assertEquals("fake_token_premium_monthly", result.purchaseToken)
        assertEquals(testProduct, engine.purchasedProduct)

        val state = engine.purchaseState.first()
        assertIs<PurchaseState.Purchased>(state)
        assertEquals("premium_monthly", state.productId)
    }

    @Test
    fun purchase_failure_returns_error() = runTest {
        val engine = FakeBillingEngine()
        engine.shouldFail = true

        val result = engine.purchase(testProduct)
        assertIs<PurchaseResult.Error>(result)
    }

    @Test
    fun queryProducts_filters_by_id() = runTest {
        val engine = FakeBillingEngine()
        val second = testProduct.copy(id = "premium_yearly", name = "Premium Yearly")
        engine.setProducts(listOf(testProduct, second))

        val result = engine.queryProducts(listOf("premium_monthly"))
        assertEquals(1, result.size)
        assertEquals("premium_monthly", result[0].id)
        assertEquals(listOf("premium_monthly"), engine.queriedProductIds)
    }

    @Test
    fun queryProducts_throws_on_failure() = runTest {
        val engine = FakeBillingEngine()
        engine.shouldFail = true

        var threw = false
        try {
            engine.queryProducts(listOf("id"))
        } catch (_: RuntimeException) {
            threw = true
        }
        assertTrue(threw)
    }

    @Test
    fun restorePurchases_success() = runTest {
        val engine = FakeBillingEngine()
        val result = engine.restorePurchases()

        assertIs<PurchaseResult.Success>(result)
        assertTrue(engine.restoreCalled)
    }

    @Test
    fun restorePurchases_failure() = runTest {
        val engine = FakeBillingEngine()
        engine.shouldFail = true

        val result = engine.restorePurchases()
        assertIs<PurchaseResult.Error>(result)
        assertTrue(engine.restoreCalled)
    }

    @Test
    fun consumePurchase_success_and_failure() = runTest {
        val engine = FakeBillingEngine()
        assertTrue(engine.consumePurchase("token_123"))
        assertEquals("token_123", engine.consumedToken)

        engine.shouldFail = true
        assertFalse(engine.consumePurchase("token_456"))
    }

    @Test
    fun setProducts_updates_flow() = runTest {
        val engine = FakeBillingEngine()
        assertTrue(engine.availableProducts.first().isEmpty())

        engine.setProducts(listOf(testProduct))
        assertEquals(1, engine.availableProducts.first().size)
        assertEquals("premium_monthly", engine.availableProducts.first()[0].id)
    }

    @Test
    fun setPurchaseState_updates_flow() = runTest {
        val engine = FakeBillingEngine()
        assertIs<PurchaseState.Idle>(engine.purchaseState.first())

        engine.setPurchaseState(PurchaseState.Loading)
        assertIs<PurchaseState.Loading>(engine.purchaseState.first())
    }
}
