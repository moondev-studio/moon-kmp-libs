package com.moondeveloper.auth

import kotlinx.coroutines.flow.Flow

interface AuthProvider {
    val currentUser: Flow<AuthUser?>

    suspend fun signInWithEmail(email: String, password: String): AuthResult<AuthUser>
    suspend fun signUpWithEmail(email: String, password: String): AuthResult<AuthUser>
    suspend fun signInWithGoogle(idToken: String): AuthResult<AuthUser>
    suspend fun signInWithApple(idToken: String, nonce: String): AuthResult<AuthUser>
    suspend fun signOut(): AuthResult<Unit>
    suspend fun deleteAccount(): AuthResult<Unit>
    suspend fun changePassword(currentPassword: String, newPassword: String): AuthResult<Unit>
    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit>
    fun getCurrentUser(): AuthUser?
    fun isSignedIn(): Boolean
}
