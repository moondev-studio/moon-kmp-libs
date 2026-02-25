package com.moondeveloper.i18n

/** Base interface for translation data sources. */
interface TranslationSource {
    suspend fun getTranslations(locale: AppLocale): Map<String, String>?
    suspend fun isAvailable(locale: AppLocale): Boolean
}

/** Marker interface for app-bundled translation sources. */
interface BundledTranslationSource : TranslationSource

/** Remote translation source with download-on-demand and caching. */
interface RemoteTranslationSource : TranslationSource {
    suspend fun download(locale: AppLocale): Boolean
    suspend fun getCachedLocales(): List<AppLocale>
    suspend fun clearCache()
}
