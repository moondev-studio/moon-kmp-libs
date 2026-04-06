package com.moondeveloper.liveactivity

import kotlinx.serialization.Serializable

/**
 * Live Activity display data.
 * Module handles data transfer only; UI is defined at app level.
 */
@Serializable
data class LiveActivityData(
    val title: String,
    val subtitle: String = "",
    val primaryValue: String,
    val secondaryValue: String = "",
    val iconEmoji: String = "",
    val progressFraction: Float = 0f,
    val isOngoing: Boolean = true
)

/** Live Activity state */
enum class LiveActivityState {
    IDLE,
    ACTIVE,
    ENDED,
    ERROR
}

/** Platform capability detection result */
data class LiveActivityCapability(
    val isDynamicIslandSupported: Boolean,
    val isNowBarSupported: Boolean,
    val isOngoingNotificationSupported: Boolean
)
