package com.moondeveloper.billing

import kotlinx.coroutines.flow.Flow

/**
 * Manages premium subscription state and promo code redemption.
 *
 * @see NoOpPremiumStateManager for unsupported platforms
 * @see FakePremiumStateManager for testing
 */
interface PremiumStateManager {
    /** Reactive stream of the current premium tier. */
    val premiumTier: Flow<PremiumTier>

    /** Check and return the current premium status. */
    suspend fun checkPremiumStatus(): PremiumTier

    /** Grant temporary premium access for the specified duration. */
    suspend fun grantTemporaryAccess(durationMinutes: Int)

    /** Redeem a promo code. Returns `true` if successful. */
    suspend fun redeemPromoCode(code: String): Boolean
}

/** Premium subscription tier levels. */
enum class PremiumTier {
    FREE,
    PREMIUM,
    PREMIUM_PLUS
}
