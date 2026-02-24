package com.moondeveloper.i18n

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HybridTranslationProviderTest {

    @Test
    fun getString_returns_bundled_translation() = runTest {
        val bundled = FakeBundledSource(mapOf(
            "en" to mapOf("hello" to "Hello", "bye" to "Goodbye"),
            "ko" to mapOf("hello" to "안녕하세요", "bye" to "안녕히 가세요")
        ))
        val remote = FakeRemoteSource()
        val provider = HybridTranslationProvider(bundled, remote)
        provider.initialize()
        provider.setLocale(AppLocale("ko"))

        assertEquals("안녕하세요", provider.getString("hello"))
    }

    @Test
    fun getString_falls_back_to_default_locale() = runTest {
        val bundled = FakeBundledSource(mapOf(
            "en" to mapOf("hello" to "Hello")
        ))
        val remote = FakeRemoteSource()
        val provider = HybridTranslationProvider(bundled, remote)
        provider.initialize()
        provider.setLocale(AppLocale("ja"))

        assertEquals("Hello", provider.getString("hello"))
    }

    @Test
    fun getString_with_params() = runTest {
        val bundled = FakeBundledSource(mapOf(
            "en" to mapOf("welcome" to "Hello {0}, you have {1} items")
        ))
        val provider = HybridTranslationProvider(bundled, FakeRemoteSource())
        provider.initialize()

        assertEquals("Hello Alice, you have 5 items", provider.getString("welcome", "Alice", 5))
    }

    @Test
    fun getString_returns_key_when_not_found() = runTest {
        val bundled = FakeBundledSource(mapOf("en" to emptyMap()))
        val provider = HybridTranslationProvider(bundled, FakeRemoteSource())
        provider.initialize()

        assertEquals("unknown_key", provider.getString("unknown_key"))
    }

    @Test
    fun remote_source_used_when_bundled_unavailable() = runTest {
        val bundled = FakeBundledSource(mapOf(
            "en" to mapOf("hello" to "Hello")
        ))
        val remote = FakeRemoteSource(mapOf(
            "ja" to mapOf("hello" to "こんにちは")
        ))
        val provider = HybridTranslationProvider(bundled, remote)
        provider.initialize()
        provider.setLocale(AppLocale("ja"))

        assertEquals("こんにちは", provider.getString("hello"))
    }

    @Test
    fun getPlural_returns_correct_form() = runTest {
        val bundled = FakeBundledSource(mapOf(
            "en" to mapOf(
                "items_zero" to "No items",
                "items_one" to "{count} item",
                "items_other" to "{count} items"
            )
        ))
        val provider = HybridTranslationProvider(bundled, FakeRemoteSource())
        provider.initialize()

        assertEquals("No items", provider.getPlural("items", 0))
        assertEquals("1 item", provider.getPlural("items", 1))
        assertEquals("5 items", provider.getPlural("items", 5))
    }

    @Test
    fun appLocale_tag_format() {
        assertEquals("en", AppLocale("en").tag)
        assertEquals("ko-KR", AppLocale("ko", "KR").tag)
        assertEquals("zh-CN", AppLocale("zh", "CN").tag)
    }
}

// === Fakes ===

private class FakeBundledSource(
    private val data: Map<String, Map<String, String>>
) : BundledTranslationSource {
    override suspend fun getTranslations(locale: AppLocale) = data[locale.languageCode]
    override suspend fun isAvailable(locale: AppLocale) = locale.languageCode in data
}

private class FakeRemoteSource(
    private val data: Map<String, Map<String, String>> = emptyMap()
) : RemoteTranslationSource {
    override suspend fun getTranslations(locale: AppLocale) = data[locale.languageCode]
    override suspend fun isAvailable(locale: AppLocale) = locale.languageCode in data
    override suspend fun download(locale: AppLocale) = locale.languageCode in data
    override suspend fun getCachedLocales() = data.keys.map { AppLocale(it) }
    override suspend fun clearCache() {}
}
