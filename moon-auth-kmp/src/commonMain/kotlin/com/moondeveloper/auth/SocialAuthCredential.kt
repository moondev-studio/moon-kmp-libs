package com.moondeveloper.auth

data class SocialAuthCredential(
    val idToken: String,
    val accessToken: String? = null,
    val nonce: String? = null
)
