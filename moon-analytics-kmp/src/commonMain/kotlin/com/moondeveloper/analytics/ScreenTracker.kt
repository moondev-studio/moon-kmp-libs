package com.moondeveloper.analytics

interface ScreenTracker {
    fun trackScreenView(screenName: String, screenClass: String? = null)
}
