package com.moondeveloper.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class AuthUserTest {

    @Test
    fun default_values() {
        val user = AuthUser(uid = "u1", email = "a@b.com", displayName = "A", photoUrl = null)
        assertFalse(user.isEmailVerified)
        assertFalse(user.isAnonymous)
        assertEquals(AuthProviderType.EMAIL, user.provider)
    }

    @Test
    fun all_provider_types() {
        assertEquals(4, AuthProviderType.entries.size)
        assertEquals("EMAIL", AuthProviderType.EMAIL.name)
        assertEquals("GOOGLE", AuthProviderType.GOOGLE.name)
        assertEquals("APPLE", AuthProviderType.APPLE.name)
        assertEquals("ANONYMOUS", AuthProviderType.ANONYMOUS.name)
    }

    @Test
    fun social_auth_credential_defaults() {
        val cred = SocialAuthCredential(idToken = "tok")
        assertNull(cred.accessToken)
        assertNull(cred.nonce)
    }

    @Test
    fun social_auth_credential_with_all_fields() {
        val cred = SocialAuthCredential(idToken = "tok", accessToken = "acc", nonce = "n")
        assertEquals("tok", cred.idToken)
        assertEquals("acc", cred.accessToken)
        assertEquals("n", cred.nonce)
    }
}
