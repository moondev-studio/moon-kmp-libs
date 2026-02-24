package com.moondeveloper.billing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

object NoOpPremiumStateManager : PremiumStateManager {
    override val premiumTier: Flow<PremiumTier> = MutableStateFlow(PremiumTier.FREE)
    override suspend fun checkPremiumStatus(): PremiumTier = PremiumTier.FREE
    override suspend fun grantTemporaryAccess(durationMinutes: Int) {}
    override suspend fun redeemPromoCode(code: String): Boolean = false
}
