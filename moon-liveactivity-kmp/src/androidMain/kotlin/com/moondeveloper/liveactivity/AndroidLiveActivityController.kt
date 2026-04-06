package com.moondeveloper.liveactivity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * Android implementation of [LiveActivityController].
 * Uses Ongoing Notification (Samsung Now Bar requires private SDK, so fallback to notification).
 *
 * @param context Application context
 * @param smallIconResId Resource ID for notification small icon (required by Android)
 */
class AndroidLiveActivityController(
    private val context: Context,
    private val smallIconResId: Int
) : LiveActivityController {

    companion object {
        private const val CHANNEL_ID = "moon_live_activity"
        private const val CHANNEL_NAME = "Live Activity"
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val activeNotifications = mutableMapOf<String, Int>()
    private val states = mutableMapOf<String, LiveActivityState>()
    private var notificationIdCounter = 10000

    init {
        createNotificationChannel()
    }

    override fun getCapability(): LiveActivityCapability {
        val isSamsungDevice = Build.MANUFACTURER.equals("samsung", ignoreCase = true)
        val isSamsungNowBar = isSamsungDevice && Build.VERSION.SDK_INT >= 34
        return LiveActivityCapability(
            isDynamicIslandSupported = false,
            isNowBarSupported = isSamsungNowBar,
            isOngoingNotificationSupported = Build.VERSION.SDK_INT >= 26
        )
    }

    override fun start(id: String, data: LiveActivityData) {
        val notifId = notificationIdCounter++
        activeNotifications[id] = notifId
        states[id] = LiveActivityState.ACTIVE

        val notification = buildNotification(data)
        notificationManager.notify(notifId, notification)
    }

    override fun update(id: String, data: LiveActivityData) {
        if (states[id] != LiveActivityState.ACTIVE) return
        val notifId = activeNotifications[id] ?: return

        val notification = buildNotification(data)
        notificationManager.notify(notifId, notification)
    }

    override fun end(id: String) {
        val notifId = activeNotifications.remove(id) ?: return
        notificationManager.cancel(notifId)
        states[id] = LiveActivityState.ENDED
    }

    override fun endAll() {
        activeNotifications.keys.toList().forEach { end(it) }
    }

    override fun getState(id: String): LiveActivityState {
        return states[id] ?: LiveActivityState.IDLE
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Live activity updates"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(data: LiveActivityData): Notification {
        val title = if (data.iconEmoji.isNotEmpty()) {
            "${data.iconEmoji} ${data.title}"
        } else {
            data.title
        }

        val text = buildString {
            append(data.primaryValue)
            if (data.secondaryValue.isNotEmpty()) {
                append("  ")
                append(data.secondaryValue)
            }
        }

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .apply {
                if (data.subtitle.isNotEmpty()) {
                    setSubText(data.subtitle)
                }
            }
            .setSmallIcon(smallIconResId)
            .setOngoing(data.isOngoing)
            .setOnlyAlertOnce(true)
            .apply {
                if (data.progressFraction > 0f) {
                    setProgress(100, (data.progressFraction * 100).toInt(), false)
                }
            }
            .build()
    }
}
