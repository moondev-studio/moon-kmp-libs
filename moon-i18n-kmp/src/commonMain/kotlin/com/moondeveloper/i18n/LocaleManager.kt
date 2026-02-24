package com.moondeveloper.i18n

interface LocaleManager {
    fun getSystemLocale(): AppLocale
    fun setAppLocale(locale: AppLocale)
}
