package com.moondeveloper.i18n

class NoOpLocaleManager : LocaleManager {
    override fun getSystemLocale(): AppLocale = AppLocale("en")
    override fun setAppLocale(locale: AppLocale) {}
}
