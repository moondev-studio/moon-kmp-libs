package com.moondeveloper.i18n

/**
 * Represents a locale with language and optional country code.
 *
 * @property languageCode ISO 639-1 language code (e.g., "en", "ko")
 * @property countryCode Optional ISO 3166-1 alpha-2 country code (e.g., "US", "KR")
 * @property displayName Human-readable name (defaults to [languageCode])
 * @property tag BCP 47 language tag (e.g., "ko-KR", "en")
 */
data class AppLocale(
    val languageCode: String,
    val countryCode: String? = null,
    val displayName: String = languageCode
) {
    val tag: String get() = if (countryCode != null) "$languageCode-$countryCode" else languageCode
}
