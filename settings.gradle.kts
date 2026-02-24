pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "moon-kmp-libs"

include(":moon-analytics-kmp")
include(":moon-sync-kmp")
include(":moon-ui-kmp")
include(":moon-auth-kmp")
include(":moon-billing-kmp")
include(":moon-i18n-kmp")
