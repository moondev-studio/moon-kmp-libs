package com.moondeveloper.auth.firebase

import com.moondeveloper.auth.AuthException
import com.moondeveloper.auth.AuthProvider
import com.moondeveloper.auth.AuthProviderType
import com.moondeveloper.auth.AuthResult
import com.moondeveloper.auth.AuthUser
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.OAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Firebase-backed [AuthProvider] using GitLive KMP Firebase SDK.
 *
 * Supports email/password, Google, and Apple sign-in via [FirebaseAuth].
 * Provider type is resolved from the user's providerData rather than hard-coded.
 */
class FirebaseAuthProvider(
    private val auth: FirebaseAuth = Firebase.auth
) : AuthProvider {

    override val currentUser: Flow<AuthUser?>
        get() = auth.authStateChanged.map { it?.toAuthUser() }

    override suspend fun signInWithEmail(email: String, password: String): AuthResult<AuthUser> =
        runAuth { auth.signInWithEmailAndPassword(email, password).user?.toAuthUser() }

    override suspend fun signUpWithEmail(email: String, password: String): AuthResult<AuthUser> =
        runAuth { auth.createUserWithEmailAndPassword(email, password).user?.toAuthUser() }

    override suspend fun signInWithGoogle(idToken: String): AuthResult<AuthUser> = runAuth {
        val credential = GoogleAuthProvider.credential(idToken, null)
        auth.signInWithCredential(credential).user?.toAuthUser()
    }

    override suspend fun signInWithApple(idToken: String, nonce: String): AuthResult<AuthUser> = runAuth {
        val credential = OAuthProvider.credential(
            providerId = "apple.com",
            idToken = idToken,
            rawNonce = nonce
        )
        auth.signInWithCredential(credential).user?.toAuthUser()
    }

    override suspend fun signOut(): AuthResult<Unit> = runAuth { auth.signOut() }

    override suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> =
        runAuth { auth.sendPasswordResetEmail(email) }

    override fun getCurrentUser(): AuthUser? = auth.currentUser?.toAuthUser()

    override fun isSignedIn(): Boolean = auth.currentUser != null

    override suspend fun changePassword(currentPassword: String, newPassword: String): AuthResult<Unit> =
        runAuth {
            val user = auth.currentUser ?: throw Exception("User is not signed in")
            val email = user.email ?: throw Exception("Could not retrieve email info")
            val credential = EmailAuthProvider.credential(email, currentPassword)
            user.reauthenticate(credential)
            user.updatePassword(newPassword)
        }

    override suspend fun deleteAccount(): AuthResult<Unit> =
        runAuth { auth.currentUser?.delete() ?: throw Exception("No user to delete") }

    // --- Internal helpers ---

    private inline fun <reified T> runAuth(block: () -> T?): AuthResult<T> = runCatching {
        block() ?: throw Exception("Could not retrieve result")
    }.fold(
        onSuccess = { AuthResult.Success(it) },
        onFailure = { AuthResult.Error(it.toAuthException()) }
    )

    /**
     * Convert a [FirebaseUser] to an [AuthUser].
     * Resolves [AuthProviderType] from providerData instead of hard-coding EMAIL.
     */
    private fun FirebaseUser.toAuthUser(): AuthUser {
        val resolvedProvider = resolveProviderType()
        return AuthUser(
            uid = uid,
            email = email,
            displayName = displayName,
            photoUrl = photoURL,
            isEmailVerified = isEmailVerified,
            isAnonymous = isAnonymous,
            provider = resolvedProvider
        )
    }

    /**
     * Determine the actual [AuthProviderType] by inspecting providerData.
     * Falls back to EMAIL if no recognized provider is found.
     */
    private fun FirebaseUser.resolveProviderType(): AuthProviderType = when {
        isAnonymous -> AuthProviderType.ANONYMOUS
        providerData.any { it.providerId == "google.com" } -> AuthProviderType.GOOGLE
        providerData.any { it.providerId == "apple.com" } -> AuthProviderType.APPLE
        else -> AuthProviderType.EMAIL
    }

    private fun Throwable.toAuthException(): AuthException {
        val msg = message ?: return AuthException.Unknown("Unknown error", this)
        return when {
            msg.contains("INVALID_EMAIL", ignoreCase = true) -> AuthException.InvalidEmail(this)
            msg.contains("WEAK_PASSWORD", ignoreCase = true) -> AuthException.WeakPassword(this)
            msg.contains("USER_NOT_FOUND", ignoreCase = true) -> AuthException.UserNotFound(this)
            msg.contains("WRONG_PASSWORD", ignoreCase = true) -> AuthException.WrongPassword(this)
            msg.contains("INVALID_PASSWORD", ignoreCase = true) -> AuthException.WrongPassword(this)
            msg.contains("EMAIL_EXISTS", ignoreCase = true) -> AuthException.EmailAlreadyInUse(this)
            msg.contains("EMAIL_ALREADY_IN_USE", ignoreCase = true) -> AuthException.EmailAlreadyInUse(this)
            msg.contains("NETWORK", ignoreCase = true) -> AuthException.NetworkError(this)
            msg.contains("USER_DISABLED", ignoreCase = true) -> AuthException.UserDisabled(this)
            msg.contains("TOO_MANY_REQUESTS", ignoreCase = true) -> AuthException.TooManyRequests(this)
            msg.contains("OPERATION_NOT_ALLOWED", ignoreCase = true) -> AuthException.OperationNotAllowed(this)
            msg.contains("cancel", ignoreCase = true) -> AuthException.Cancelled(this)
            else -> AuthException.Unknown(msg, this)
        }
    }
}
