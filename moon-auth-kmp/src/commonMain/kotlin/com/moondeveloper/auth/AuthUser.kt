package com.moondeveloper.auth

/**
 * Authenticated user data.
 *
 * @property uid Unique user identifier
 * @property email User's email address, if available
 * @property displayName User's display name, if available
 * @property photoUrl URL to the user's profile photo, if available
 * @property isEmailVerified Whether the email has been verified
 * @property isAnonymous Whether this is an anonymous/guest user
 * @property provider The authentication provider used
 */
data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean = false,
    val isAnonymous: Boolean = false,
    val provider: AuthProviderType = AuthProviderType.EMAIL
)

/** Authentication provider type. */
enum class AuthProviderType {
    EMAIL, GOOGLE, APPLE, ANONYMOUS
}
