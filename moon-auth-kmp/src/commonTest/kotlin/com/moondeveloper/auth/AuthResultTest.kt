package com.moondeveloper.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthResultTest {

    @Test
    fun success_holds_data() {
        val user = AuthUser(uid = "u1", email = "a@b.com", displayName = "Test", photoUrl = null)
        val result: AuthResult<AuthUser> = AuthResult.Success(user)

        assertIs<AuthResult.Success<AuthUser>>(result)
        assertEquals("u1", result.data.uid)
        assertEquals("a@b.com", result.data.email)
    }

    @Test
    fun error_holds_exception() {
        val result: AuthResult<AuthUser> = AuthResult.Error(AuthException.InvalidCredentials())

        assertIs<AuthResult.Error>(result)
        assertIs<AuthException.InvalidCredentials>(result.exception)
        assertEquals("Invalid credentials", result.exception.message)
    }

    @Test
    fun all_exception_types_have_correct_messages() {
        val exceptions = listOf(
            AuthException.InvalidCredentials() to "Invalid credentials",
            AuthException.InvalidEmail() to "Invalid email format",
            AuthException.UserNotFound() to "User not found",
            AuthException.WrongPassword() to "Wrong password",
            AuthException.EmailAlreadyInUse() to "Email already in use",
            AuthException.WeakPassword() to "Weak password",
            AuthException.UserDisabled() to "User disabled",
            AuthException.TooManyRequests() to "Too many requests",
            AuthException.OperationNotAllowed() to "Operation not allowed",
            AuthException.Cancelled() to "Authentication cancelled",
            AuthException.NetworkError() to "Network error",
            AuthException.Unknown() to "Unknown error",
        )

        for ((exception, expectedMessage) in exceptions) {
            assertEquals(expectedMessage, exception.message)
        }
    }

    @Test
    fun unknown_exception_accepts_custom_message() {
        val exception = AuthException.Unknown("Custom error message")
        assertEquals("Custom error message", exception.message)
    }

    @Test
    fun exception_preserves_cause() {
        val cause = RuntimeException("root cause")
        val exception = AuthException.NetworkError(cause)
        assertEquals(cause, exception.cause)
    }

    @Test
    fun success_unit_result() {
        val result: AuthResult<Unit> = AuthResult.Success(Unit)
        assertIs<AuthResult.Success<Unit>>(result)
    }
}
