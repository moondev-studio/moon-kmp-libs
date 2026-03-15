package com.moondeveloper.i18n

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NoOpImplementationsTest {

    @Test
    fun noOpLocaleManager_returns_english() {
        val manager = NoOpLocaleManager()
        assertEquals("en", manager.getSystemLocale().languageCode)
    }

    @Test
    fun noOpLocaleManager_setAppLocale_does_nothing() {
        val manager = NoOpLocaleManager()
        manager.setAppLocale(AppLocale("ko"))
        // No exception, still returns en
        assertEquals("en", manager.getSystemLocale().languageCode)
    }

    @Test
    fun noOpTranslationProvider_returns_key_as_value() {
        val provider = NoOpTranslationProvider()
        assertEquals("greeting", provider.getString("greeting"))
    }

    @Test
    fun noOpTranslationProvider_returns_key_with_args() {
        val provider = NoOpTranslationProvider()
        assertEquals("welcome", provider.getString("welcome", "Alice"))
    }

    @Test
    fun noOpTranslationProvider_getPlural_returns_key() {
        val provider = NoOpTranslationProvider()
        assertEquals("item_count", provider.getPlural("item_count", 5))
    }

    @Test
    fun noOpTranslationProvider_available_locales_empty() {
        val provider = NoOpTranslationProvider()
        assertTrue(provider.getAvailableLocales().isEmpty())
    }
}
