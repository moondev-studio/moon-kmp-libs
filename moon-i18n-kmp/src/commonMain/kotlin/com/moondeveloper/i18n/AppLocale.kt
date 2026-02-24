package com.moondeveloper.i18n

data class AppLocale(
    val languageCode: String,
    val countryCode: String? = null,
    val displayName: String = languageCode
) {
    val tag: String get() = if (countryCode != null) "$languageCode-$countryCode" else languageCode
}
