package com.moondeveloper.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAuthProvider : AuthProvider {

    private data class RegisteredUser(
        val email: String,
        val password: String,
        val user: AuthUser
    )

    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    override val currentUser: Flow<AuthUser?> = _currentUser

    private val registeredUsers = mutableListOf<RegisteredUser>()
    private val socialTokens = mutableMapOf<String, AuthUser>()
    private var pendingError: AuthException? = null

    val passwordResetEmails = mutableListOf<String>()

    fun registerUser(email: String, password: String, user: AuthUser) {
        registeredUsers.add(RegisteredUser(email, password, user))
    }

    fun configureSocialLogin(token: String, user: AuthUser) {
        socialTokens[token] = user
    }

    fun simulateError(error: AuthException) {
        pendingError = error
    }

    private fun consumeError(): AuthException? {
        val error = pendingError
        pendingError = null
        return error
    }

    override suspend fun signInWithEmail(email: String, password: String): AuthResult<AuthUser> {
        consumeError()?.let { return AuthResult.Error(it) }
        val registered = registeredUsers.find { it.email == email }
            ?: return AuthResult.Error(AuthException.UserNotFound())
        if (registered.password != password) {
            return AuthResult.Error(AuthException.WrongPassword())
        }
        _currentUser.value = registered.user
        return AuthResult.Success(registered.user)
    }

    override suspend fun signUpWithEmail(email: String, password: String): AuthResult<AuthUser> {
        consumeError()?.let { return AuthResult.Error(it) }
        if (registeredUsers.any { it.email == email }) {
            return AuthResult.Error(AuthException.EmailAlreadyInUse())
        }
        val user = AuthUser(
            uid = "fake-${email.hashCode()}",
            email = email,
            displayName = null,
            photoUrl = null,
            provider = AuthProviderType.EMAIL
        )
        registeredUsers.add(RegisteredUser(email, password, user))
        _currentUser.value = user
        return AuthResult.Success(user)
    }

    override suspend fun signInWithGoogle(idToken: String): AuthResult<AuthUser> {
        consumeError()?.let { return AuthResult.Error(it) }
        val user = socialTokens[idToken]
            ?: return AuthResult.Error(AuthException.InvalidCredentials())
        _currentUser.value = user
        return AuthResult.Success(user)
    }

    override suspend fun signInWithApple(idToken: String, nonce: String): AuthResult<AuthUser> {
        consumeError()?.let { return AuthResult.Error(it) }
        val user = socialTokens[idToken]
            ?: return AuthResult.Error(AuthException.InvalidCredentials())
        _currentUser.value = user
        return AuthResult.Success(user)
    }

    override suspend fun signOut(): AuthResult<Unit> {
        consumeError()?.let { return AuthResult.Error(it) }
        _currentUser.value = null
        return AuthResult.Success(Unit)
    }

    override suspend fun deleteAccount(): AuthResult<Unit> {
        consumeError()?.let { return AuthResult.Error(it) }
        val user = _currentUser.value
            ?: return AuthResult.Error(AuthException.UserNotFound())
        registeredUsers.removeAll { it.user.uid == user.uid }
        _currentUser.value = null
        return AuthResult.Success(Unit)
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): AuthResult<Unit> {
        consumeError()?.let { return AuthResult.Error(it) }
        val user = _currentUser.value
            ?: return AuthResult.Error(AuthException.UserNotFound())
        val index = registeredUsers.indexOfFirst { it.user.uid == user.uid }
        if (index < 0) return AuthResult.Error(AuthException.UserNotFound())
        val registered = registeredUsers[index]
        if (registered.password != currentPassword) {
            return AuthResult.Error(AuthException.WrongPassword())
        }
        registeredUsers[index] = registered.copy(password = newPassword)
        return AuthResult.Success(Unit)
    }

    override suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        consumeError()?.let { return AuthResult.Error(it) }
        if (registeredUsers.none { it.email == email }) {
            return AuthResult.Error(AuthException.UserNotFound())
        }
        passwordResetEmails.add(email)
        return AuthResult.Success(Unit)
    }

    override fun getCurrentUser(): AuthUser? = _currentUser.value

    override fun isSignedIn(): Boolean = _currentUser.value != null
}
