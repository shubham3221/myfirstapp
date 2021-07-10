package com.example.myfirstapp.notifications

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.content.ContextCompat

class MyService_NEW : Service() {
    private val mBinder: IBinder = MyBinder()


    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    inner class MyBinder : Binder() {
        val service: MyService_NEW
            get() = this@MyService_NEW
    }
    fun startDownload(id:Int){
        val notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager
        notificationManager.showNotification(id,"notification body",this)


    }
}