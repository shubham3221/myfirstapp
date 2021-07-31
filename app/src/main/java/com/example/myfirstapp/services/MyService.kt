package com.example.myfirstapp.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.myfirstapp.Myconstants
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R

class MyService : Service() {
    val TAG = "MyService"
    private var mChannel: NotificationChannel? = null
    private var notifManager: NotificationManager? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: " )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread{
            for (i in 0..500){
                Thread.sleep(1000)
                Log.d(TAG, "onStartCommand: $i" )
            }
        }.start()
        startForeground(1001,forground_notification()!!)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: " )

    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun displayCustomNotificationForOrders(title: String, description: String) {
        if (notifManager == null) {
            notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(this, ServiceActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val importance = NotificationManager.IMPORTANCE_HIGH
            if (mChannel == null) {
                mChannel = NotificationChannel("0", title, importance)
                mChannel!!.setDescription(description)
                mChannel!!.enableVibration(true)
                notifManager!!.createNotificationChannel(mChannel!!)
            }
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "0")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_ONE_SHOT)
            builder.setContentTitle(title)
                .setSmallIcon(getNotificationIcon()) // required
                .setContentText(description) // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(resources, android.R.mipmap.sym_def_app_icon))
//                .setBadgeIconType(R.mipmap.sym_def_app_icon)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            val notification: Notification = builder.build()
            notifManager!!.notify(0, notification)
        } else {
            val intent = Intent(this, ServiceActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            var pendingIntent: PendingIntent? = null
            pendingIntent =
                PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_ONE_SHOT)
            val defaultSoundUri: Uri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(description)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(baseContext, android.R.color.background_dark))
                .setSound(defaultSoundUri)
                .setSmallIcon(getNotificationIcon())
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(
                    description))
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1251, notificationBuilder.build())
        }
    }

    private fun getNotificationIcon(): Int {
        val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) android.R.mipmap.sym_def_app_icon else android.R.mipmap.sym_def_app_icon
    }

    private fun forground_notification() : Notification? {
        if (Build.VERSION.SDK_INT >= 26) {
//            val CHANNEL_ID = "foreground"
//            val channel = NotificationChannel(CHANNEL_ID,
//                "My Foreground Service Channel",
//                NotificationManager.IMPORTANCE_DEFAULT)
//            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
//                channel)
            val mnotification = NotificationCompat.Builder(this, Myconstants.CHANNAL_ID)
                .setContentTitle("A service is running in the background")
                .setContentText("Foreground Service Running")
            Intent().apply {
                this.action = "stop_service"
                val broadcast = PendingIntent.getBroadcast(this@MyService,
                    12345,
                    this,
                    PendingIntent.FLAG_UPDATE_CURRENT)
                mnotification.addAction(R.mipmap.ic_launcher, "stop_service", broadcast)
            }
            return mnotification.build()
        }
        return null

    }


}