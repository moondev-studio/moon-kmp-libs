package com.moondeveloper.auth

data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean = false,
    val isAnonymous: Boolean = false,
    val provider: AuthProviderType = AuthProviderType.EMAIL
)

enum class AuthProviderType {
    EMAIL, GOOGLE, APPLE, ANONYMOUS
}
