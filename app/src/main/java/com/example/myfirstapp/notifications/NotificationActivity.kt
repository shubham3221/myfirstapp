package com.example.myfirstapp.notifications

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.myfirstapp.Myconstants
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import com.example.myfirstapp.googlesdk.GoogleActivity
import kotlinx.android.synthetic.main.activity_image.view.*
import kotlinx.android.synthetic.main.activity_notification.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NotificationActivity : AppCompatActivity() {

    private var jobid = 2001
    private var not_pause: Boolean = true
    private var mChannel: NotificationChannel? = null
    private var notifManager: NotificationManager? = null
    private lateinit var notificationManager: NotificationManagerCompat

    var mService: MyService_OLD? = null
    var mIsBound: Boolean? = null
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MyService_OLD.MyBinder
            mService = binder.service
            mIsBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mIsBound = false
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        EventBus.getDefault().register(this)
        if (isMyServiceRunning(MyService_OLD::class.java)) start_service.text = "Stop Service"

        Log.e(TAG, "onCreate: is service running? : " + isMyServiceRunning(MyService_OLD::class.java))

        start_service.setOnClickListener {
            if (isMyServiceRunning(MyService_OLD::class.java)){
                start_service.text = "Start Service"
                stopService(Intent(this, MyService_OLD::class.java))
            }else{
                val intent = Intent(this, MyService_OLD::class.java)
                startService(intent)
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                start_service.text = "Stop Service"
            }

//            startDownload(2001)
//            displayCustomNotificationForOrders("title 1","first notifiaction",1001)
//            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    Log.e(TAG, "Fetching FCM registration token failed", task.exception)
//                    return@OnCompleteListener
//                }
//
//                // Get new FCM registration token
//                val token = task.result
//                Log.e(TAG, "onCreate: "+token )
//
//
//            })

        }


        start_download.setOnClickListener {
            mService?.let {
                it.createNotification(++jobid)
            }
//            displayCustomNotificationForOrders("title 2", "second notifiaction", 1002)
        }


        update_first.setOnClickListener {
            displayCustomNotificationForOrders("title 1", "first notifiaction updated", 1001)
        }

        update_second.setOnClickListener {
            val notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager
            notificationManager.showNotification(1001,"notification body",this)
        }
    }


    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun displayCustomNotificationForOrders(title: String, description: String, id: Int) {
        if (notifManager == null) {
            notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val intent = Intent(this, TwitterActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
////            val importance = NotificationManager.IMPORTANCE_HIGH
//            val importance = NotificationManager.IMPORTANCE_LOW
//            if (mChannel == null) {
//                mChannel = NotificationChannel("0", title, importance)
//                mChannel!!.setDescription(description)
//                mChannel!!.enableVibration(true)
//                notifManager!!.createNotificationChannel(mChannel!!)
//            }
//            val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, Myconstants.CHANNAL_ID)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
//                    Intent.FLAG_ACTIVITY_SINGLE_TOP
//            val pendingIntent: PendingIntent =
//                PendingIntent.getActivity(this, id, intent, PendingIntent.FLAG_ONE_SHOT)
//            builder.setContentTitle(title)
//                .setSmallIcon(getNotificationIcon()) // required
//                .setContentText(description) // required
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setAutoCancel(true)
//                .setOngoing(true)
//                .setLargeIcon(BitmapFactory.decodeResource(resources,
//                    android.R.mipmap.sym_def_app_icon))
////                .setBadgeIconType(R.mipmap.sym_def_app_icon)
//                .setContentIntent(pendingIntent)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//            val notification: Notification = builder.build()
//            notifManager!!.notify(id, notification)
//        } else {


            val intent = Intent(this, GoogleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent: PendingIntent? = PendingIntent.getActivity(this, id, intent, PendingIntent.FLAG_ONE_SHOT)
            val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this,Myconstants.CHANNAL_ID)
                .setContentTitle(title)
                .setContentText(description)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(baseContext, android.R.color.background_dark))
                .setSound(defaultSoundUri)
                .setSmallIcon(getNotificationIcon())
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(
                    description))
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            Intent(this,MyBroadcastReceiver::class.java).apply {
                this.action = "a"
                val broadcast = PendingIntent.getBroadcast(this@NotificationActivity,
                    id,
                    this,
                    PendingIntent.FLAG_UPDATE_CURRENT)
                notificationBuilder.addAction(R.mipmap.ic_launcher, "button 1", broadcast)
            }
            notificationManager.notify(id, notificationBuilder.build())
//        }
    }

    private fun getNotificationIcon(): Int {
        val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) android.R.mipmap.sym_def_app_icon else android.R.mipmap.sym_def_app_icon
    }


    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun getNotify(dataPicker: DataPicker) {
        Log.e(TAG, "getNotify: ")
        AlertDialog.Builder(this).setMessage(dataPicker.data).create().show()
    }
}