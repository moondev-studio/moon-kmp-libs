package com.moondeveloper.liveactivity

import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIDevice

/**
 * iOS implementation of [LiveActivityController].
 *
 * ActivityKit's core classes (Activity<Attributes>, ActivityAttributes) use Swift generics
 * that cannot be called directly from Kotlin/Native.
 *
 * Strategy:
 * 1. Swift bridge file (in app's iosApp/) handles actual ActivityKit calls
 * 2. KMP module sends data via NSNotificationCenter
 * 3. Swift code subscribes to these notifications and invokes ActivityKit
 *
 * See Swift bridge template in module documentation.
 */
class IosLiveActivityController : LiveActivityController {

    companion object {
        private const val NOTIFICATION_START = "MoonLiveActivity_Start"
        private const val NOTIFICATION_UPDATE = "MoonLiveActivity_Update"
        private const val NOTIFICATION_END = "MoonLiveActivity_End"
    }

    private val states = mutableMapOf<String, LiveActivityState>()

    override fun getCapability(): LiveActivityCapability {
        val systemVersion = UIDevice.currentDevice.systemVersion
        val majorVersion = systemVersion.split(".").firstOrNull()?.toIntOrNull() ?: 0
        return LiveActivityCapability(
            isDynamicIslandSupported = majorVersion >= 16,
            isNowBarSupported = false,
            isOngoingNotificationSupported = false
        )
    }

    override fun start(id: String, data: LiveActivityData) {
        val payload: Map<Any?, Any?> = mapOf(
            "id" to id,
            "title" to data.title,
            "subtitle" to data.subtitle,
            "primaryValue" to data.primaryValue,
            "secondaryValue" to data.secondaryValue,
            "iconEmoji" to data.iconEmoji,
            "progressFraction" to data.progressFraction.toString(),
            "isOngoing" to data.isOngoing.toString()
        )

        // Store in UserDefaults for Swift bridge to read
        val defaults = NSUserDefaults.standardUserDefaults
        defaults.setObject(payload, "${NOTIFICATION_START}_$id")
        defaults.synchronize()

        // Post notification to Swift bridge
        NSNotificationCenter.defaultCenter.postNotificationName(
            NOTIFICATION_START,
            `object` = id,
            userInfo = payload
        )

        states[id] = LiveActivityState.ACTIVE
    }

    override fun update(id: String, data: LiveActivityData) {
        if (states[id] != LiveActivityState.ACTIVE) return

        val payload: Map<Any?, Any?> = mapOf(
            "id" to id,
            "primaryValue" to data.primaryValue,
            "secondaryValue" to data.secondaryValue,
            "progressFraction" to data.progressFraction.toString()
        )

        NSNotificationCenter.defaultCenter.postNotificationName(
            NOTIFICATION_UPDATE,
            `object` = id,
            userInfo = payload
        )
    }

    override fun end(id: String) {
        NSNotificationCenter.defaultCenter.postNotificationName(
            NOTIFICATION_END,
            `object` = id,
            userInfo = null
        )

        states[id] = LiveActivityState.ENDED
    }

    override fun endAll() {
        states.keys
            .filter { states[it] == LiveActivityState.ACTIVE }
            .forEach { end(it) }
    }

    override fun getState(id: String): LiveActivityState {
        return states[id] ?: LiveActivityState.IDLE
    }
}
