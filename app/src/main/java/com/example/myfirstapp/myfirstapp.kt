package com.example.myfirstapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.res.Configuration
import android.os.Build
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*

class myfirstapp : Application() {
    override fun onCreate() {
        super.onCreate()
//        LocaleUtilsFirst().setLocale(this,"es")
        createNotificationChannels()
    }

    //Check if the Android version is greater than 8. (Android Oreo)
    private fun createNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                Myconstants.CHANNAL_ID,
                Myconstants.CHANNAL_NAME,
                //IMPORTANCE_HIGH = shows a notification as peek notification.
                //IMPORTANCE_LOW = shows the notification in the status bar.
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }
            channel1.description = "Progress Notification Channel"
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel1)
        }
    }
}