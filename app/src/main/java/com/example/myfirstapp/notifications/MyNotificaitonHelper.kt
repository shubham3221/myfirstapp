package com.example.myfirstapp.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


class MyNotificaitonHelper(context: Context): ContextWrapper(context) {

    lateinit var mManager: NotificationManager
    private val IOS_CHANNEL_ID: String? = "IOS_CHANNAL_ID"
    private val NOTIFICATION_CHANNEL_ID = "10001"
    val ANDROID_CHANNEL_NAME = "ANDROID CHANNEL"
    val IOS_CHANNEL_NAME = "IOS CHANNEL"
    private var mContext: Context = context
    private var mNotificationManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannels() {

        // create android channel
        val androidChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
            ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        // Sets whether notifications posted to this channel should display notification lights
        androidChannel.enableLights(true)
        // Sets whether notification posted to this channel should vibrate.
        androidChannel.enableVibration(true)
        // Sets the notification light color for notifications posted to this channel
        androidChannel.lightColor = Color.GREEN
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        androidChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        getManager().createNotificationChannel(androidChannel)

        // create ios channel
        val iosChannel = NotificationChannel(IOS_CHANNEL_ID,
            IOS_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        iosChannel.enableLights(true)
        iosChannel.enableVibration(true)
        iosChannel.lightColor = Color.GRAY
        iosChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        getManager().createNotificationChannel(iosChannel)
    }

    private fun getManager(): NotificationManager {
        if (!::mManager.isInitialized) {
            mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return mManager
    }
}