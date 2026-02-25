package com.moondeveloper.auth

/**
 * Credential data from a social authentication provider (Google, Apple).
 *
 * @property idToken The provider's ID token
 * @property accessToken Optional access token
 * @property nonce Optional nonce for Apple Sign-In
 */
data class SocialAuthCredential(
    val idToken: String,
    val accessToken: String? = null,
    val nonce: String? = null
)
