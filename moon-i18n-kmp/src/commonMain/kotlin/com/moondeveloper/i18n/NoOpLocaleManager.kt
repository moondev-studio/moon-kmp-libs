package com.moondeveloper.i18n

/** No-op [LocaleManager] that always returns English locale. */
class NoOpLocaleManager : LocaleManager {
    override fun getSystemLocale(): AppLocale = AppLocale("en")
    override fun setAppLocale(locale: AppLocale) {}
}
