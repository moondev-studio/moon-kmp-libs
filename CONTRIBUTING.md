# Contributing to Moon KMP Libraries

Thank you for your interest in contributing!

## Development Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/sun941003/moon-kmp-libs.git
   cd moon-kmp-libs
   ```

2. Create `local.properties` with your Android SDK path:
   ```properties
   sdk.dir=/path/to/android/sdk
   ```

3. Build all modules:
   ```bash
   ./gradlew build
   ```

4. Run tests (Desktop JVM):
   ```bash
   ./gradlew desktopTest
   ```

## Guidelines

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Add KDoc to all public APIs
- Write tests for new features (target: desktopTest)
- Keep OSS modules dependency-free (no Firebase, Play Services, etc.)
- One module per PR when possible

## Module Structure

Each module follows the convention plugin pattern:

```
moon-{name}-kmp/
├── build.gradle.kts          # Uses moon.kmp.library or moon.compose.library
└── src/
    ├── commonMain/kotlin/     # Interfaces and shared code
    └── commonTest/kotlin/     # Unit tests
```

## Pull Requests

1. Fork the repository
2. Create a feature branch from `main`
3. Make your changes
4. Run `./gradlew desktopTest` to verify
5. Submit a PR with a clear description

## Reporting Issues

Use [GitHub Issues](https://github.com/sun941003/moon-kmp-libs/issues) with:
- Module name (e.g., moon-auth-kmp)
- Expected vs actual behavior
- Minimal reproduction steps
- Kotlin/Compose Multiplatform versions

## License

By contributing, you agree that your contributions will be licensed under the Apache License 2.0.
