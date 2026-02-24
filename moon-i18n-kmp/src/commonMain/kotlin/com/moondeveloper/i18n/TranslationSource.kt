package com.moondeveloper.i18n

interface TranslationSource {
    suspend fun getTranslations(locale: AppLocale): Map<String, String>?
    suspend fun isAvailable(locale: AppLocale): Boolean
}

interface BundledTranslationSource : TranslationSource

interface RemoteTranslationSource : TranslationSource {
    suspend fun download(locale: AppLocale): Boolean
    suspend fun getCachedLocales(): List<AppLocale>
    suspend fun clearCache()
}
