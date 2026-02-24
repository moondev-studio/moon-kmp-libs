package com.moondeveloper.billing

import kotlinx.coroutines.flow.Flow

interface PremiumStateManager {
    val premiumTier: Flow<PremiumTier>

    suspend fun checkPremiumStatus(): PremiumTier
    suspend fun grantTemporaryAccess(durationMinutes: Int)
    suspend fun redeemPromoCode(code: String): Boolean
}

enum class PremiumTier {
    FREE,
    PREMIUM,
    PREMIUM_PLUS
}
