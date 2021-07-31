package com.example.myfirstapp.notifications

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myfirstapp.Myconstants.Companion.CHANNAL_ID
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

data class NotificationData(
    var progress: Int,
    var jobs: Job,
    var notification: NotificationCompat.Builder
)

open class MyService_OLD : Service() {
    private val mBinder: IBinder = MyBinder()
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var broadcastReceiver: MyBroadcastReceiver
    private lateinit var notification: NotificationCompat.Builder
    private var progressMap = mutableMapOf<Int, Int>()
    private var jobs = mutableMapOf<Int, Job>()
    var jobss = mutableListOf<NotificationData>()
    private val coroutineJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + coroutineJob)

    inner class MyBinder : Binder() {
        val service: MyService_OLD
            get() = this@MyService_OLD
    }


    override fun onBind(intent: Intent): IBinder {
        Log.e(TAG, "onBind: ")
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("//", "onStartCommand: startid: " + startId)
//        forground_notification()?.build()?.let {
//            startForeground(1, it)
//        }
        return START_STICKY
    }
    fun createNotification(id: Int){
        create_Notification(id,false)
    }

    fun create_Notification(id: Int , showNotifiaction:Boolean): NotificationCompat.Builder? {
        if (!showNotifiaction){
            jobs.put(id, downloadFile(id, null))
            return null
        }
        if (!::notificationManager.isInitialized) notificationManager =
            NotificationManagerCompat.from(this)
        val intent = Intent(this, NotificationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, id, intent, 0)

        //Sets the maximum progress as 100
        val progressMax = 100
        //Creating a notification and setting its various attributes
        notification = NotificationCompat.Builder(this, CHANNAL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Downloading")
//                .setContentText("Downloading")
                .setContentTitle("file downloading")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setProgress(progressMax, 0, true)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        addButtonNotification("pause", id)

        //Initial Alert
        jobs.put(id, downloadFile(id, notification))
        Log.e(TAG, "create_Notification: " + jobs)
        return notification
    }

    @SuppressLint("RestrictedApi")
    private fun addButtonNotification(action: String, id: Int) {
        Log.e(TAG, "addButtonNotification: " + id)
        notification.mActions.clear()
        Intent().apply {
            this.action = action
            this.putExtra("id", id)
            val broadcast = PendingIntent.getBroadcast(this@MyService_OLD,
                id,
                this,
                PendingIntent.FLAG_UPDATE_CURRENT)
            notification.addAction(R.mipmap.ic_launcher, action, broadcast)
        }
        Intent().apply {
            this.action = "stop"
            this.putExtra("id", id)
            val broadcast = PendingIntent.getBroadcast(this@MyService_OLD,
                id,
                this,
                PendingIntent.FLAG_UPDATE_CURRENT)
            notification.addAction(R.mipmap.ic_launcher, "stop", broadcast)
        }
        notificationManager.notify(id, notification.build())
    }

    @SuppressLint("RestrictedApi")
    fun downloadFile(id: Int, notification: NotificationCompat.Builder?) : Job{
        return  scope.launch {
            var process = 0
            if (progressMap.get(id) != null){
                process = progressMap.get(id)!!
            }
            while (process != 100 && isActive) {
                delay(200)
                process += 2
                progressMap.put(id, process)
                notification?.setContentText("$process%")
                    ?.setProgress(100, process, false)
                notification?.let { notificationManager.notify(id, it.build()) }
                EventBus.getDefault().post(ProgressUpdates(process))
            }
            launch {
                Log.e(TAG, "download complete: removing id $id")
                jobs.remove(id)
                progressMap.remove(id)
                notification?.setContentText("Download complete")
                    ?.setProgress(0, 0, false)
                    ?.setOngoing(false)
                notification?.mActions?.clear()
                notification?.let { notificationManager.notify(id, it.build()) }
                if (jobs.isEmpty()){
                    stopForeground(true)
                    stopSelf()
                } else {
                    Log.e(TAG, "downloadFile: jobs exists: size: ${jobs.size}")
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventActionTrigger(jobData: JobData) {
        when(jobData.command){

            "stop" -> {
                Log.e(TAG, "eventActionTrigger: stop " + jobData.id)
                jobs.get(jobData.id)!!.cancel()
                notificationManager.cancel(jobData.id)
                jobs.remove(jobData.id)
                if (jobs.isEmpty()) {
                    Log.e(TAG, "eventActionTrigger: stop self called")
                    stopForeground(true)
                    stopSelf()
                } else {
                    Log.e(TAG, "eventActionTrigger: job is not empty$jobs")
                }
            }
            "pause" -> {
                Log.e(TAG, "eventActionTrigger: pause job id: " + jobData.id)
                jobs.get(jobData.id)!!.cancel()
//                jobs.remove(jobData.id)
                addButtonNotification("resume", jobData.id)
            }

            "resume" -> {
                Log.e(TAG, "eventActionTrigger: resume job id: " + jobData.id)
                jobs.set(jobData.id, downloadFile(jobData.id, notification))
                addButtonNotification("pause", jobData.id)
            }

            "stop_service" -> {
                jobs.forEach {
                    jobs.get(it.key)?.let { runningJob ->
                        runningJob.cancel()
                    }
                    stopSelf()
                    stopForeground(true)
                    notificationManager.cancel(jobData.id)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreate: service")
        EventBus.getDefault().register(this)
        broadcastReceiver = MyBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("pause")
        intentFilter.addAction("resume")
        intentFilter.addAction("stop")
        registerReceiver(broadcastReceiver, intentFilter)

        forground_notification()?.build()?.let {
            startForeground(1, it)
        }
    }

    private fun forground_notification() : NotificationCompat.Builder? {
        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "foreground"
            val channel = NotificationChannel(CHANNEL_ID,
                "My Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel)
            val mnotification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("A service is running in the background")
                .setContentText("Foreground Service Running")
            Intent().apply {
                this.action = "stop_service"
                val broadcast = PendingIntent.getBroadcast(this@MyService_OLD,
                    12345,
                    this,
                    PendingIntent.FLAG_UPDATE_CURRENT)
                mnotification.addAction(R.mipmap.ic_launcher, "stop_service", broadcast)
            }
            return mnotification
        }
        return null

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy: service")
        coroutineJob.cancel()
        EventBus.getDefault().unregister(this)
        unregisterReceiver(broadcastReceiver)
    }
}