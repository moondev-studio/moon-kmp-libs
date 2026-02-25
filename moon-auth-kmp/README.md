# moon-auth-kmp

Platform-agnostic authentication provider abstraction for Kotlin Multiplatform.

## Features

- **Email/password** sign in, sign up, password reset
- **Social auth** (Google, Apple) via ID token
- **Reactive current user** as `Flow<AuthUser?>`
- **Typed error handling** with `AuthResult<T>` and 12 `AuthException` subtypes
- **NoOp and Fake** implementations for testing

## Installation

```kotlin
// includeBuild (local development)
implementation("com.moondeveloper:moon-auth-kmp")

// Maven Central (coming soon)
implementation("com.moondeveloper:moon-auth-kmp:1.0.0")
```

## Quick Start

```kotlin
val authProvider: AuthProvider = get() // from Koin

// Sign in
when (val result = authProvider.signInWithEmail("user@example.com", "password")) {
    is AuthResult.Success -> println("Welcome ${result.data.displayName}")
    is AuthResult.Error -> when (result.exception) {
        is AuthException.WrongPassword -> showError("Wrong password")
        is AuthException.UserNotFound -> showError("No account found")
        else -> showError(result.exception.message)
    }
}

// Observe auth state
authProvider.currentUser.collect { user ->
    if (user != null) navigateToHome() else navigateToLogin()
}
```

## API Overview

| Type | Description |
|------|-------------|
| `AuthProvider` | Core auth interface (email, social, password management) |
| `AuthUser` | User data (uid, email, displayName, photoUrl, provider) |
| `AuthResult<T>` | Sealed class: `Success<T>` / `Error` |
| `AuthException` | 12 typed exceptions (InvalidCredentials, UserNotFound, etc.) |
| `AuthProviderType` | Enum: EMAIL, GOOGLE, APPLE, ANONYMOUS |
| `SocialAuthCredential` | ID token + optional access token and nonce |
| `NoOpAuthProvider` | No-op implementation (all operations succeed with empty user) |
| `FakeAuthProvider` | In-memory fake for unit testing |

## Platform Support

| Platform | Status |
|----------|--------|
| Android | Supported |
| iOS | Supported |
| Desktop (JVM) | Supported |

## License

Apache License 2.0
