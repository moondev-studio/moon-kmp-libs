package com.moondeveloper.auth

/**
 * Result type for authentication operations.
 *
 * @param T The success data type
 */
sealed class AuthResult<out T> {
    /** Successful operation with result data. */
    data class Success<T>(val data: T) : AuthResult<T>()

    /** Failed operation with a typed [AuthException]. */
    data class Error(val exception: AuthException) : AuthResult<Nothing>()
}

/**
 * Typed authentication exceptions for structured error handling.
 *
 * 12 subtypes covering all common auth failure scenarios.
 */
sealed class AuthException(override val message: String, cause: Throwable? = null) : Exception(message, cause) {
    class InvalidCredentials(cause: Throwable? = null) : AuthException("Invalid credentials", cause)
    class InvalidEmail(cause: Throwable? = null) : AuthException("Invalid email format", cause)
    class UserNotFound(cause: Throwable? = null) : AuthException("User not found", cause)
    class WrongPassword(cause: Throwable? = null) : AuthException("Wrong password", cause)
    class EmailAlreadyInUse(cause: Throwable? = null) : AuthException("Email already in use", cause)
    class WeakPassword(cause: Throwable? = null) : AuthException("Weak password", cause)
    class UserDisabled(cause: Throwable? = null) : AuthException("User disabled", cause)
    class TooManyRequests(cause: Throwable? = null) : AuthException("Too many requests", cause)
    class OperationNotAllowed(cause: Throwable? = null) : AuthException("Operation not allowed", cause)
    class Cancelled(cause: Throwable? = null) : AuthException("Authentication cancelled", cause)
    class NetworkError(cause: Throwable? = null) : AuthException("Network error", cause)
    class Unknown(msg: String = "Unknown error", cause: Throwable? = null) : AuthException(msg, cause)
}
