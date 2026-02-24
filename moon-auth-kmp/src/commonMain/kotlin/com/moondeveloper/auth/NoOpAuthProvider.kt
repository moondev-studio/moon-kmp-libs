package com.moondeveloper.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

object NoOpAuthProvider : AuthProvider {
    override val currentUser: Flow<AuthUser?> = MutableStateFlow(null)
    override suspend fun signInWithEmail(email: String, password: String): AuthResult<AuthUser> = AuthResult.Error(AuthException.Unknown("NoOp"))
    override suspend fun signUpWithEmail(email: String, password: String): AuthResult<AuthUser> = AuthResult.Error(AuthException.Unknown("NoOp"))
    override suspend fun signInWithGoogle(idToken: String): AuthResult<AuthUser> = AuthResult.Error(AuthException.Unknown("NoOp"))
    override suspend fun signInWithApple(idToken: String, nonce: String): AuthResult<AuthUser> = AuthResult.Error(AuthException.Unknown("NoOp"))
    override suspend fun signOut(): AuthResult<Unit> = AuthResult.Success(Unit)
    override suspend fun deleteAccount(): AuthResult<Unit> = AuthResult.Error(AuthException.Unknown("NoOp"))
    override suspend fun changePassword(currentPassword: String, newPassword: String): AuthResult<Unit> = AuthResult.Error(AuthException.Unknown("NoOp"))
    override suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> = AuthResult.Error(AuthException.Unknown("NoOp"))
    override fun getCurrentUser(): AuthUser? = null
    override fun isSignedIn(): Boolean = false
}
