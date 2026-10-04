package com.example.premiumapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.premiumapp.core.di.AppContainer

class App : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val mediaChannel = NotificationChannel(
                CHANNEL_MEDIA,
                getString(R.string.notification_channel_media),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_media_desc)
            }

            val alertsChannel = NotificationChannel(
                CHANNEL_ALERTS,
                getString(R.string.notification_channel_alerts),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.notification_channel_alerts_desc)
            }

            notificationManager.createNotificationChannel(mediaChannel)
            notificationManager.createNotificationChannel(alertsChannel)
        }
    }

    companion object {
        const val CHANNEL_MEDIA = "lumina_media_ops"
        const val CHANNEL_ALERTS = "lumina_alerts"

        fun getContainer(context: Context): AppContainer {
            return (context.applicationContext as App).container
        }
    }
}
