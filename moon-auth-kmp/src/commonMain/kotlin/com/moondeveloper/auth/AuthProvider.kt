package com.moondeveloper.auth

import kotlinx.coroutines.flow.Flow

/**
 * Platform-agnostic authentication provider.
 *
 * Supports email/password, Google, and Apple sign-in.
 * All operations return [AuthResult] with typed [AuthException] errors.
 *
 * @see NoOpAuthProvider for unsupported platforms
 * @see FakeAuthProvider for unit testing
 */
interface AuthProvider {
    /** Reactive stream of the current authenticated user, or `null` if signed out. */
    val currentUser: Flow<AuthUser?>

    /** Sign in with email and password. */
    suspend fun signInWithEmail(email: String, password: String): AuthResult<AuthUser>

    /** Create a new account with email and password. */
    suspend fun signUpWithEmail(email: String, password: String): AuthResult<AuthUser>

    /** Sign in with a Google ID token. */
    suspend fun signInWithGoogle(idToken: String): AuthResult<AuthUser>

    /** Sign in with an Apple ID token and nonce. */
    suspend fun signInWithApple(idToken: String, nonce: String): AuthResult<AuthUser>

    /** Sign out the current user. */
    suspend fun signOut(): AuthResult<Unit>

    /** Permanently delete the current user's account. */
    suspend fun deleteAccount(): AuthResult<Unit>

    /** Change the current user's password. Requires re-authentication. */
    suspend fun changePassword(currentPassword: String, newPassword: String): AuthResult<Unit>

    /** Send a password reset email. */
    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit>

    /** Get the current user synchronously. Returns `null` if signed out. */
    fun getCurrentUser(): AuthUser?

    /** Check if a user is currently signed in. */
    fun isSignedIn(): Boolean
}
