package com.moondeveloper.i18n

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class AppLocaleTest {

    @Test
    fun tag_with_country_code() {
        val locale = AppLocale("ko", "KR")
        assertEquals("ko-KR", locale.tag)
    }

    @Test
    fun tag_without_country_code() {
        val locale = AppLocale("en")
        assertEquals("en", locale.tag)
    }

    @Test
    fun default_display_name_is_language_code() {
        val locale = AppLocale("ja")
        assertEquals("ja", locale.displayName)
    }

    @Test
    fun custom_display_name() {
        val locale = AppLocale("ko", "KR", displayName = "한국어")
        assertEquals("한국어", locale.displayName)
    }

    @Test
    fun country_code_defaults_to_null() {
        val locale = AppLocale("fr")
        assertNull(locale.countryCode)
    }

    @Test
    fun data_class_equality() {
        val a = AppLocale("en", "US")
        val b = AppLocale("en", "US")
        assertEquals(a, b)
    }

    @Test
    fun different_country_code_not_equal() {
        val us = AppLocale("en", "US")
        val gb = AppLocale("en", "GB")
        assertNotEquals(us, gb)
    }
}
