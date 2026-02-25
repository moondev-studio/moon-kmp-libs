package com.moondeveloper.i18n

/**
 * Platform-agnostic locale management.
 *
 * Detects the system locale and allows switching the app locale at runtime.
 *
 * @see NoOpLocaleManager for testing
 */
interface LocaleManager {
    /** Get the device's current system locale. */
    fun getSystemLocale(): AppLocale

    /** Set the app's locale, triggering language change. */
    fun setAppLocale(locale: AppLocale)
}
