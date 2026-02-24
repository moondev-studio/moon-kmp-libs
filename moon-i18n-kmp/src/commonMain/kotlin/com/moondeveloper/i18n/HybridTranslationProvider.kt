package com.moondeveloper.i18n

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HybridTranslationProvider(
    private val bundledSource: BundledTranslationSource,
    private val remoteSource: RemoteTranslationSource,
    private val fallbackLocale: AppLocale = AppLocale("en")
) : TranslationProvider {

    private val _currentLocale = MutableStateFlow(fallbackLocale)
    override val currentLocale: Flow<AppLocale> = _currentLocale.asStateFlow()

    private var translations: Map<String, String> = emptyMap()
    private var fallbackTranslations: Map<String, String> = emptyMap()

    override fun getString(key: String): String {
        return translations[key]
            ?: fallbackTranslations[key]
            ?: key
    }

    override fun getString(key: String, vararg args: Any): String {
        val template = getString(key)
        return args.foldIndexed(template) { index, acc, arg ->
            acc.replace("{$index}", arg.toString())
        }
    }

    override fun getPlural(key: String, count: Int): String {
        val pluralKey = when {
            count == 0 -> "${key}_zero"
            count == 1 -> "${key}_one"
            else -> "${key}_other"
        }
        return getString(pluralKey).replace("{count}", count.toString())
    }

    override suspend fun setLocale(locale: AppLocale) {
        val bundled = bundledSource.getTranslations(locale)
        if (bundled != null) {
            translations = bundled
            _currentLocale.value = locale
            return
        }

        val remote = remoteSource.getTranslations(locale)
        if (remote != null) {
            translations = remote
            _currentLocale.value = locale
            return
        }

        if (remoteSource.download(locale)) {
            val downloaded = remoteSource.getTranslations(locale)
            if (downloaded != null) {
                translations = downloaded
                _currentLocale.value = locale
                return
            }
        }

        if (locale != fallbackLocale) {
            setLocale(fallbackLocale)
        }
    }

    override fun getAvailableLocales(): List<AppLocale> = emptyList()

    suspend fun initialize() {
        fallbackTranslations = bundledSource.getTranslations(fallbackLocale) ?: emptyMap()
    }
}
