package com.moondeveloper.auth

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertNull

class NoOpAuthProviderTest {

    @Test
    fun currentUser_emits_null() = runTest {
        val user = NoOpAuthProvider.currentUser.first()
        assertNull(user)
    }

    @Test
    fun getCurrentUser_returns_null() {
        assertNull(NoOpAuthProvider.getCurrentUser())
    }

    @Test
    fun isSignedIn_returns_false() {
        assertFalse(NoOpAuthProvider.isSignedIn())
    }

    @Test
    fun signInWithEmail_returns_error() = runTest {
        val result = NoOpAuthProvider.signInWithEmail("a@b.com", "pass")
        assertIs<AuthResult.Error>(result)
    }

    @Test
    fun signUpWithEmail_returns_error() = runTest {
        val result = NoOpAuthProvider.signUpWithEmail("a@b.com", "pass")
        assertIs<AuthResult.Error>(result)
    }

    @Test
    fun signInWithGoogle_returns_error() = runTest {
        val result = NoOpAuthProvider.signInWithGoogle("token")
        assertIs<AuthResult.Error>(result)
    }

    @Test
    fun signInWithApple_returns_error() = runTest {
        val result = NoOpAuthProvider.signInWithApple("token", "nonce")
        assertIs<AuthResult.Error>(result)
    }

    @Test
    fun signOut_returns_success() = runTest {
        val result = NoOpAuthProvider.signOut()
        assertIs<AuthResult.Success<Unit>>(result)
    }

    @Test
    fun deleteAccount_returns_error() = runTest {
        val result = NoOpAuthProvider.deleteAccount()
        assertIs<AuthResult.Error>(result)
    }

    @Test
    fun changePassword_returns_error() = runTest {
        val result = NoOpAuthProvider.changePassword("old", "new")
        assertIs<AuthResult.Error>(result)
    }

    @Test
    fun sendPasswordResetEmail_returns_error() = runTest {
        val result = NoOpAuthProvider.sendPasswordResetEmail("a@b.com")
        assertIs<AuthResult.Error>(result)
    }
}
