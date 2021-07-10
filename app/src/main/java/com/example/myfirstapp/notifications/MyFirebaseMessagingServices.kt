package com.example.myfirstapp.notifications

import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.googlesdk.GoogleActivity
import com.example.myfirstapp.twitterLogin.TwitterActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

data class DataPicker(var data:String)


class MyFirebaseMessagingServices : FirebaseMessagingService() {
    private var mChannel: NotificationChannel? = null
    private var notifManager: NotificationManager? = null
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        p0.notification?.let {
            val title = p0.notification!!.title
            val body = p0.notification!!.body

            Log.e(TAG, "onMessageReceived: " + Thread.currentThread().name + title)

            EventBus.getDefault().post(DataPicker(body!!))

//            Handler(Looper.getMainLooper()).post(Runnable {
//                AlertDialog.Builder(this).setMessage(title).create().show()
//            })


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                displayCustomNotificationForOrders(title!!, body!!)
            }
        }

        if (p0.data.isNotEmpty()){
            Log.e(TAG, "onMessageReceived: data received")
        }


    }
//    private fun showCustomPopupMenu() {
//        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
//        val layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val view: View = layoutInflater.inflate(R.layout.mypopup, null)
//        val layoutParams = WindowManager.LayoutParams(
//            WindowManager.LayoutParams.WRAP_CONTENT,
//            WindowManager.LayoutParams.WRAP_CONTENT,
//            WindowManager.LayoutParams.TYPE_PHONE,
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//            PixelFormat.TRANSLUCENT)
//        layoutParams.gravity = Gravity.CENTER or Gravity.CENTER
//        layoutParams.x = 0
//        layoutParams.y = 0
//        windowManager.addView(view, layoutParams)
//    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()

    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e(TAG, "onNewToken: " + p0)
    }
    @RequiresApi(Build.VERSION_CODES.M)
     fun displayCustomNotificationForOrders(title: String, description: String) {
        if (notifManager == null) {
            notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(this, TwitterActivity::class.java)
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
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.sym_def_app_icon))
//                .setBadgeIconType(R.mipmap.sym_def_app_icon)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            val notification: Notification = builder.build()
            notifManager!!.notify(0, notification)
        } else {
            val intent = Intent(this, GoogleActivity::class.java)
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
                .setColor(ContextCompat.getColor(baseContext, R.color.background_dark))
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
        return if (useWhiteIcon) R.mipmap.sym_def_app_icon else R.mipmap.sym_def_app_icon
    }
}