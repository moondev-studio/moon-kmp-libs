package com.moondeveloper.i18n

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class NoOpTranslationProvider : TranslationProvider {
    override val currentLocale: Flow<AppLocale> = MutableStateFlow(AppLocale("en"))
    override fun getString(key: String): String = key
    override fun getString(key: String, vararg args: Any): String = key
    override fun getPlural(key: String, count: Int): String = key
    override suspend fun setLocale(locale: AppLocale) {}
    override fun getAvailableLocales(): List<AppLocale> = emptyList()
}
