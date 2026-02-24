package com.moondeveloper.i18n

import kotlinx.coroutines.flow.Flow

interface TranslationProvider {
    val currentLocale: Flow<AppLocale>
    fun getString(key: String): String
    fun getString(key: String, vararg args: Any): String
    fun getPlural(key: String, count: Int): String
    suspend fun setLocale(locale: AppLocale)
    fun getAvailableLocales(): List<AppLocale>
}
