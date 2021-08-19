package com.example.myfirstapp.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.annotation.IdRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myfirstapp.Myconstants
import com.example.myfirstapp.R


class NotificationsUtil(private val context: Context,
                        private val channelName: String = Myconstants.CHANNAL_NAME,
                        private val channelID: String=Myconstants.CHANNAL_ID,
                        private val channelDescription: String = Myconstants.CHANNAL_DESC,
                        private val sound: Boolean = false,
                        @IdRes rawNotificationSound: Int = -1) {


    private var builder: NotificationCompat.Builder? = null
    var managerCompat: NotificationManagerCompat? = null
    private var soundUri: Uri? = null

    init {
        if (sound) {
            soundUri = Uri.parse("android.resource://" + context.packageName + "/" + rawNotificationSound)
        }
    }


    fun createNotification(id: String, message: String, title: String): Notification? {
        builder = NotificationCompat.Builder(context, createNotificationChannel())
        managerCompat = NotificationManagerCompat.from(context)

        // val pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder
            ?.setDefaults(Notification.DEFAULT_LIGHTS)
            ?.setSmallIcon(R.drawable.ic_launcher_foreground)
            ?.setContentTitle(title)?.setContentText(message)
            ?.setPriority(NotificationCompat.PRIORITY_HIGH)
            ?.setAutoCancel(true)
            ?.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            ?.setColorized(true)
            ?.setOnlyAlertOnce(true)

        //   ?.setContentIntent(pendingIntent)
        if (sound) {
            builder?.setSound(soundUri)
        }

        val notification = builder?.build()

        notification?.let { managerCompat?.notify(id.toInt(), it) }

        return notification


    }

    fun buildNotificationWithService(message: String, title: String, clazz: Class<*>): Notification? {
        builder = NotificationCompat.Builder(context, Myconstants.CHANNAL_ID)
        managerCompat = NotificationManagerCompat.from(context)

        // val pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder
            ?.setDefaults(Notification.DEFAULT_LIGHTS)
            ?.setSmallIcon(R.mipmap.ic_launcher)
            ?.setContentTitle(title)?.setContentText(message)
            ?.setPriority(NotificationCompat.PRIORITY_HIGH)
            ?.setAutoCancel(true)
            ?.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            ?.setColorized(true)
            ?.setOnlyAlertOnce(true)


        //   ?.setContentIntent(pendingIntent)
        if (sound) {
            builder?.setSound(soundUri)
        }
        Intent(context, clazz).apply {
            this.action = Myconstants.STOP_SERVICE
            val broadcast = PendingIntent.getService(context,
                    3221,
                    this,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            builder!!.addAction(R.mipmap.ic_launcher, "Stop Service", broadcast)
        }

        return builder?.build()
    }


    fun createExpandableNotification(id: String, message: String, title: String, @IdRes notificationDrawable: Int): Notification? {
        builder = NotificationCompat.Builder(context, createNotificationChannel())
        managerCompat = NotificationManagerCompat.from(context)

        builder?.setSmallIcon(notificationDrawable)
            ?.setContentTitle(title)
            ?.setContentText(message)
            ?.setDefaults(Notification.DEFAULT_LIGHTS)
            ?.setPriority(NotificationCompat.PRIORITY_HIGH)
            ?.setAutoCancel(true)
            ?.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            ?.setColorized(true)
            ?.setStyle(NotificationCompat.BigTextStyle().bigText(message))
            ?.setOnlyAlertOnce(true)

        if (sound) {
            builder?.setSound(soundUri)
        }

        val notification = builder?.build()

        notification?.let { managerCompat?.notify(id.toInt(), it) }

        return notification

    }

    fun createExpandableNotificationWithPendingIntent(id: String, message: String, title: String, @IdRes notificationDrawable: Int, pendingIntent: PendingIntent): Notification? {
        builder = NotificationCompat.Builder(context, createNotificationChannel())
        managerCompat = NotificationManagerCompat.from(context)

        builder?.setSmallIcon(notificationDrawable)
            ?.setContentTitle(title)
            ?.setContentText(message)
            ?.setDefaults(Notification.DEFAULT_LIGHTS)
            ?.setPriority(NotificationCompat.PRIORITY_HIGH)
            ?.setAutoCancel(true)
            ?.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            ?.setColorized(true)
            ?.setStyle(NotificationCompat.BigTextStyle().bigText(message))
            ?.setOnlyAlertOnce(true)
            ?.setContentIntent(pendingIntent)

        if (sound) {
            builder?.setSound(soundUri)
        }

        val notification = builder?.build()

        notification?.let { managerCompat?.notify(id.toInt(), it) }

        return notification

    }

    private fun createNotificationChannel(): String {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = channelName
            val description = channelDescription
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val notificationChannel = NotificationChannel(channelID, name, NotificationManager.IMPORTANCE_HIGH)
            if (sound) {
                notificationChannel.setSound(soundUri, audioAttributes)
            }
            notificationChannel.description = description
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.MAGENTA
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(notificationChannel)

        }

        return channelID

    }

}