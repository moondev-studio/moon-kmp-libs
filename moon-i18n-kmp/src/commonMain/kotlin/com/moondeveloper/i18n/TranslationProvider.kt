package com.moondeveloper.i18n

import kotlinx.coroutines.flow.Flow

/**
 * Provides translated strings by key, with support for parameterized strings and plurals.
 *
 * @see HybridTranslationProvider for the bundled + remote implementation
 * @see NoOpTranslationProvider for testing
 */
interface TranslationProvider {
    val currentLocale: Flow<AppLocale>
    fun getString(key: String): String
    fun getString(key: String, vararg args: Any): String
    fun getPlural(key: String, count: Int): String
    suspend fun setLocale(locale: AppLocale)
    fun getAvailableLocales(): List<AppLocale>
}
