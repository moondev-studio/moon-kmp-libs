package com.moondeveloper.i18n

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HybridTranslationProviderTest {

    private class FakeBundledSource(
        private val data: Map<String, Map<String, String>> = emptyMap(),
    ) : BundledTranslationSource {
        override suspend fun getTranslations(locale: AppLocale) = data[locale.languageCode]
        override suspend fun isAvailable(locale: AppLocale) = data.containsKey(locale.languageCode)
    }

    private class FakeRemoteSource(
        private val data: Map<String, Map<String, String>> = emptyMap(),
        private val downloadable: Set<String> = emptySet(),
    ) : RemoteTranslationSource {
        private val downloaded = mutableSetOf<String>()

        override suspend fun getTranslations(locale: AppLocale): Map<String, String>? {
            if (locale.languageCode in downloaded) return data[locale.languageCode]
            return data[locale.languageCode]
        }

        override suspend fun isAvailable(locale: AppLocale) =
            data.containsKey(locale.languageCode) || locale.languageCode in downloadable

        override suspend fun download(locale: AppLocale): Boolean {
            return if (locale.languageCode in downloadable) {
                downloaded.add(locale.languageCode)
                true
            } else false
        }

        override suspend fun getCachedLocales() =
            downloaded.map { AppLocale(it) }

        override suspend fun clearCache() { downloaded.clear() }
    }

    @Test
    fun getString_returns_bundled_translation() = runTest {
        val provider = HybridTranslationProvider(
            bundledSource = FakeBundledSource(mapOf("ko" to mapOf("greeting" to "안녕하세요"))),
            remoteSource = FakeRemoteSource(),
        )
        provider.initialize()
        provider.setLocale(AppLocale("ko"))

        assertEquals("안녕하세요", provider.getString("greeting"))
    }

    @Test
    fun getString_with_args_replaces_placeholders() = runTest {
        val provider = HybridTranslationProvider(
            bundledSource = FakeBundledSource(mapOf("en" to mapOf("welcome" to "Hello, {0}!"))),
            remoteSource = FakeRemoteSource(),
        )
        provider.initialize()
        provider.setLocale(AppLocale("en"))

        assertEquals("Hello, Alice!", provider.getString("welcome", "Alice"))
    }

    @Test
    fun getString_missing_key_returns_key() = runTest {
        val provider = HybridTranslationProvider(
            bundledSource = FakeBundledSource(mapOf("en" to emptyMap())),
            remoteSource = FakeRemoteSource(),
        )
        provider.initialize()

        assertEquals("nonexistent", provider.getString("nonexistent"))
    }

    @Test
    fun getPlural_returns_correct_form() = runTest {
        val provider = HybridTranslationProvider(
            bundledSource = FakeBundledSource(
                mapOf(
                    "en" to mapOf(
                        "items_zero" to "No items",
                        "items_one" to "{count} item",
                        "items_other" to "{count} items",
                    )
                )
            ),
            remoteSource = FakeRemoteSource(),
        )
        provider.initialize()
        provider.setLocale(AppLocale("en"))

        assertEquals("No items", provider.getPlural("items", 0))
        assertEquals("1 item", provider.getPlural("items", 1))
        assertEquals("5 items", provider.getPlural("items", 5))
    }

    @Test
    fun setLocale_falls_back_to_fallback_locale() = runTest {
        val provider = HybridTranslationProvider(
            bundledSource = FakeBundledSource(
                mapOf("en" to mapOf("key" to "fallback value"))
            ),
            remoteSource = FakeRemoteSource(),
            fallbackLocale = AppLocale("en"),
        )
        provider.initialize()
        provider.setLocale(AppLocale("xx"))

        assertEquals("fallback value", provider.getString("key"))
    }

    @Test
    fun initialize_loads_fallback_translations() = runTest {
        val provider = HybridTranslationProvider(
            bundledSource = FakeBundledSource(mapOf("en" to mapOf("ok" to "OK"))),
            remoteSource = FakeRemoteSource(),
            fallbackLocale = AppLocale("en"),
        )
        provider.initialize()

        assertEquals("OK", provider.getString("ok"))
    }

    @Test
    fun remote_source_used_when_bundled_unavailable() = runTest {
        val provider = HybridTranslationProvider(
            bundledSource = FakeBundledSource(mapOf("en" to mapOf("key" to "en-fallback"))),
            remoteSource = FakeRemoteSource(mapOf("fr" to mapOf("key" to "Bonjour"))),
            fallbackLocale = AppLocale("en"),
        )
        provider.initialize()
        provider.setLocale(AppLocale("fr"))

        assertEquals("Bonjour", provider.getString("key"))
    }
}
