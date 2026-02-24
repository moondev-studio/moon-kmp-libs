package com.moondeveloper.auth

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeAuthProviderTest {

    private val testUser = AuthUser(
        uid = "test-uid",
        email = "test@example.com",
        displayName = "Test User",
        photoUrl = null,
        isEmailVerified = true,
        provider = AuthProviderType.EMAIL
    )

    @Test
    fun initially_no_user() = runTest {
        val provider = FakeAuthProvider()
        assertNull(provider.currentUser.first())
        assertNull(provider.getCurrentUser())
        assertFalse(provider.isSignedIn())
    }

    @Test
    fun signInWithEmail_succeeds_when_user_registered() = runTest {
        val provider = FakeAuthProvider()
        provider.registerUser("test@example.com", "password123", testUser)

        val result = provider.signInWithEmail("test@example.com", "password123")

        assertIs<AuthResult.Success<AuthUser>>(result)
        assertEquals("test-uid", result.data.uid)
        assertTrue(provider.isSignedIn())
        assertEquals(testUser, provider.getCurrentUser())
    }

    @Test
    fun signInWithEmail_fails_with_wrong_password() = runTest {
        val provider = FakeAuthProvider()
        provider.registerUser("test@example.com", "password123", testUser)

        val result = provider.signInWithEmail("test@example.com", "wrong")

        assertIs<AuthResult.Error>(result)
        assertIs<AuthException.WrongPassword>(result.exception)
        assertFalse(provider.isSignedIn())
    }

    @Test
    fun signInWithEmail_fails_when_user_not_found() = runTest {
        val provider = FakeAuthProvider()

        val result = provider.signInWithEmail("unknown@example.com", "pass")

        assertIs<AuthResult.Error>(result)
        assertIs<AuthException.UserNotFound>(result.exception)
    }

    @Test
    fun signUpWithEmail_creates_new_user() = runTest {
        val provider = FakeAuthProvider()

        val result = provider.signUpWithEmail("new@example.com", "password123")

        assertIs<AuthResult.Success<AuthUser>>(result)
        assertEquals("new@example.com", result.data.email)
        assertTrue(provider.isSignedIn())
    }

    @Test
    fun signUpWithEmail_fails_when_email_exists() = runTest {
        val provider = FakeAuthProvider()
        provider.registerUser("test@example.com", "password123", testUser)

        val result = provider.signUpWithEmail("test@example.com", "newpass")

        assertIs<AuthResult.Error>(result)
        assertIs<AuthException.EmailAlreadyInUse>(result.exception)
    }

    @Test
    fun signOut_clears_current_user() = runTest {
        val provider = FakeAuthProvider()
        provider.registerUser("test@example.com", "password123", testUser)
        provider.signInWithEmail("test@example.com", "password123")
        assertTrue(provider.isSignedIn())

        val result = provider.signOut()

        assertIs<AuthResult.Success<Unit>>(result)
        assertFalse(provider.isSignedIn())
        assertNull(provider.getCurrentUser())
    }

    @Test
    fun signInWithGoogle_succeeds_when_configured() = runTest {
        val provider = FakeAuthProvider()
        provider.configureSocialLogin("google-token", testUser)

        val result = provider.signInWithGoogle("google-token")

        assertIs<AuthResult.Success<AuthUser>>(result)
        assertEquals("test-uid", result.data.uid)
    }

    @Test
    fun signInWithGoogle_fails_when_not_configured() = runTest {
        val provider = FakeAuthProvider()

        val result = provider.signInWithGoogle("unknown-token")

        assertIs<AuthResult.Error>(result)
        assertIs<AuthException.InvalidCredentials>(result.exception)
    }

    @Test
    fun signInWithApple_succeeds_when_configured() = runTest {
        val provider = FakeAuthProvider()
        provider.configureSocialLogin("apple-token", testUser.copy(provider = AuthProviderType.APPLE))

        val result = provider.signInWithApple("apple-token", "nonce")

        assertIs<AuthResult.Success<AuthUser>>(result)
    }

    @Test
    fun deleteAccount_removes_user() = runTest {
        val provider = FakeAuthProvider()
        provider.registerUser("test@example.com", "password123", testUser)
        provider.signInWithEmail("test@example.com", "password123")

        val result = provider.deleteAccount()

        assertIs<AuthResult.Success<Unit>>(result)
        assertFalse(provider.isSignedIn())
    }

    @Test
    fun deleteAccount_fails_when_not_signed_in() = runTest {
        val provider = FakeAuthProvider()

        val result = provider.deleteAccount()

        assertIs<AuthResult.Error>(result)
        assertIs<AuthException.UserNotFound>(result.exception)
    }

    @Test
    fun changePassword_succeeds() = runTest {
        val provider = FakeAuthProvider()
        provider.registerUser("test@example.com", "oldpass", testUser)
        provider.signInWithEmail("test@example.com", "oldpass")

        val result = provider.changePassword("oldpass", "newpass")

        assertIs<AuthResult.Success<Unit>>(result)

        // Verify new password works
        provider.signOut()
        val signInResult = provider.signInWithEmail("test@example.com", "newpass")
        assertIs<AuthResult.Success<AuthUser>>(signInResult)
    }

    @Test
    fun changePassword_fails_when_not_signed_in() = runTest {
        val provider = FakeAuthProvider()

        val result = provider.changePassword("old", "new")

        assertIs<AuthResult.Error>(result)
    }

    @Test
    fun sendPasswordResetEmail_succeeds_for_registered_user() = runTest {
        val provider = FakeAuthProvider()
        provider.registerUser("test@example.com", "password123", testUser)

        val result = provider.sendPasswordResetEmail("test@example.com")

        assertIs<AuthResult.Success<Unit>>(result)
        assertTrue(provider.passwordResetEmails.contains("test@example.com"))
    }

    @Test
    fun sendPasswordResetEmail_fails_for_unknown_user() = runTest {
        val provider = FakeAuthProvider()

        val result = provider.sendPasswordResetEmail("unknown@example.com")

        assertIs<AuthResult.Error>(result)
        assertIs<AuthException.UserNotFound>(result.exception)
    }

    @Test
    fun currentUser_flow_updates_on_sign_in_and_out() = runTest {
        val provider = FakeAuthProvider()
        provider.registerUser("test@example.com", "password123", testUser)

        assertNull(provider.currentUser.first())

        provider.signInWithEmail("test@example.com", "password123")
        assertEquals(testUser, provider.currentUser.first())

        provider.signOut()
        assertNull(provider.currentUser.first())
    }

    @Test
    fun simulateError_forces_next_operation_to_fail() = runTest {
        val provider = FakeAuthProvider()
        provider.registerUser("test@example.com", "password123", testUser)
        provider.simulateError(AuthException.NetworkError())

        val result = provider.signInWithEmail("test@example.com", "password123")

        assertIs<AuthResult.Error>(result)
        assertIs<AuthException.NetworkError>(result.exception)

        // Subsequent call should succeed (error consumed)
        val result2 = provider.signInWithEmail("test@example.com", "password123")
        assertIs<AuthResult.Success<AuthUser>>(result2)
    }
}
