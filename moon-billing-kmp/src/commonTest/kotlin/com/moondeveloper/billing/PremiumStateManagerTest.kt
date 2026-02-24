package com.moondeveloper.billing

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PremiumStateManagerTest {

    @Test
    fun noOp_premiumTier_emits_free() = runTest {
        val tier = NoOpPremiumStateManager.premiumTier.first()
        assertEquals(PremiumTier.FREE, tier)
    }

    @Test
    fun noOp_checkPremiumStatus_returns_free() = runTest {
        assertEquals(PremiumTier.FREE, NoOpPremiumStateManager.checkPremiumStatus())
    }

    @Test
    fun noOp_redeemPromoCode_returns_false() = runTest {
        assertFalse(NoOpPremiumStateManager.redeemPromoCode("PROMO123"))
    }

    @Test
    fun fake_setTier_updates_flow_and_check() = runTest {
        val manager = FakePremiumStateManager()
        assertEquals(PremiumTier.FREE, manager.premiumTier.first())

        manager.setTier(PremiumTier.PREMIUM)
        assertEquals(PremiumTier.PREMIUM, manager.premiumTier.first())
        assertEquals(PremiumTier.PREMIUM, manager.checkPremiumStatus())

        manager.setTier(PremiumTier.PREMIUM_PLUS)
        assertEquals(PremiumTier.PREMIUM_PLUS, manager.checkPremiumStatus())
    }

    @Test
    fun fake_grantTemporaryAccess_records_params() = runTest {
        val manager = FakePremiumStateManager()
        assertFalse(manager.temporaryAccessGranted)

        manager.grantTemporaryAccess(60)
        assertTrue(manager.temporaryAccessGranted)
        assertEquals(60, manager.lastGrantDuration)
    }

    @Test
    fun fake_redeemPromoCode_success() = runTest {
        val manager = FakePremiumStateManager()
        assertTrue(manager.redeemPromoCode("CODE123"))
        assertEquals("CODE123", manager.lastRedeemedCode)
    }

    @Test
    fun fake_redeemPromoCode_failure() = runTest {
        val manager = FakePremiumStateManager()
        manager.redeemShouldSucceed = false
        assertFalse(manager.redeemPromoCode("BAD"))
        assertEquals("BAD", manager.lastRedeemedCode)
    }
}
