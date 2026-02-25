package com.moondeveloper.billing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * In-memory fake [PremiumStateManager] for unit testing.
 *
 * Set tier with [setTier], control promo redemption with [redeemShouldSucceed].
 */
class FakePremiumStateManager : PremiumStateManager {
    private val _premiumTier = MutableStateFlow(PremiumTier.FREE)
    override val premiumTier: Flow<PremiumTier> = _premiumTier

    var temporaryAccessGranted = false
        private set
    var lastGrantDuration = 0
        private set
    var lastRedeemedCode: String? = null
        private set
    var redeemShouldSucceed = true

    fun setTier(tier: PremiumTier) {
        _premiumTier.value = tier
    }

    override suspend fun checkPremiumStatus(): PremiumTier = _premiumTier.value

    override suspend fun grantTemporaryAccess(durationMinutes: Int) {
        temporaryAccessGranted = true
        lastGrantDuration = durationMinutes
    }

    override suspend fun redeemPromoCode(code: String): Boolean {
        lastRedeemedCode = code
        return redeemShouldSucceed
    }
}
