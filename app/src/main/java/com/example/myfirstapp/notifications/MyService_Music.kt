package com.example.myfirstapp.notifications

import android.app.*
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myfirstapp.Myconstants
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import com.example.myfirstapp.extra.isAppInForeground
import com.example.myfirstapp.music.MusicModel
import kotlinx.coroutines.*


data class CurrentSongData(val id:Long , val model: MusicModel)
open class MyService_Music : Service() , OnPreparedListener, MediaPlayer.OnErrorListener,
    OnCompletionListener{
    private val mBinder: IBinder = MyBinder()
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var broadcastReceiver: MyBroadcastReceiver
    private lateinit var notification: NotificationCompat.Builder
    private var progressMap = mutableMapOf<Int, Int>()
    private var jobs = mutableMapOf<Int, Job>()
    var jobss = mutableListOf<NotificationData>()
    private val coroutineJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + coroutineJob)
    var player: MediaPlayer? = null
    lateinit var currentSongData : CurrentSongData

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreate: service")
//        EventBus.getDefault().register(this)
        broadcastReceiver = MyBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("pause")
        intentFilter.addAction("resume")
        intentFilter.addAction("stop")
        registerReceiver(broadcastReceiver, intentFilter)
        player = MediaPlayer()
        initMusicPlayer()
//        forground_notification()?.build()?.let {
//            startForeground(1, it)
//        }
    }



    inner class MyBinder : Binder() {
        val service: MyService_Music
            get() = this@MyService_Music
    }


    override fun onBind(intent: Intent): IBinder {
        Log.e(TAG, "onBind: ")
        return mBinder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.hasExtra("exit")!!){
            Log.e(TAG, "onStartCommand:has extra ")
            player?.stop()
            stopForeground(true)
            if (!isAppInForeground()) {
                stopSelf()
                Log.e(TAG, "app is in foreground so stopping service " )
            }
        }
        return START_NOT_STICKY
    }


    //show notification
    open fun shownotification(name: String?, path: String?) {
        val intent = Intent(applicationContext, MyService_Music::class.java)
        intent.putExtra("exit", "exit")
        val pendingIntent = PendingIntent.getService(applicationContext, 0, intent, 0)
        val builder = NotificationCompat.Builder(
            applicationContext, Myconstants.CHANNAL_ID)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentTitle(name)
        builder.setContentText(path)
        builder.addAction(R.mipmap.ic_launcher, "Stop Service", pendingIntent)
//        builder.setContentIntent(pendingIntent)
        val notification = builder.build()
        //notificationManagerCompat.notify(1,builder);
        startForeground(123, notification)
    }

    open fun initMusicPlayer() {
        player?.setWakeMode(
            applicationContext,
            PowerManager.PARTIAL_WAKE_LOCK)
        player?.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        //AudioManager.STREAM_MUSIC
        player?.setOnPreparedListener(
            this)
        player?.setOnCompletionListener(
            this)
        player?.setOnErrorListener(this)
    }

    fun playSong(id: Long, model: MusicModel){
        try {
            currentSongData = CurrentSongData(id,model)
            player?.reset()
            val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
            player?.setDataSource(applicationContext, uri)
            player?.prepareAsync()
            shownotification(model.name, model.path)
        }catch (e:Exception){
            Log.e(TAG, "playsong exception: ${e}" )
        }

    }
    fun resumePlay(){
        playSong(currentSongData.id,currentSongData.model)
    }

    fun isPlaying() = player?.isPlaying

    fun playSongold() {
        val descriptor = applicationContext.resources.assets.openFd("music/mymusic.mp3")
        try {
            player?.setDataSource(descriptor.fileDescriptor,
                descriptor.startOffset,
                descriptor.length)
            descriptor.close()
            player?.prepareAsync()
        }catch (e:Exception){
            Log.e(TAG, "playsong exception: ${e.message}" )
        }
    }
    fun playSong() {
        val descriptor = applicationContext.resources.assets.openFd("music/mymusic.mp3")
        try {
            player?.reset()
            player?.setDataSource(descriptor.fileDescriptor,
                descriptor.startOffset,
                descriptor.length)
            descriptor.close()
            player?.prepareAsync()
        }catch (e:Exception){
            Log.e(TAG, "playSong: "+e.message )
        }

    }
    fun stopPlay(){
        player?.stop()
    }

    fun pausePlay(){
        player?.pause()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        Log.e(TAG, "onPrepared: ")
        mp?.start()
//        musicInterface.StateChange(MusicState.PLAYING)
    }


    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.e(TAG, "onError: $what")
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Log.e(TAG, "onCompletion: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.stop()
        player?.release()
        player = null
        Log.e(TAG, "onDestroy: service")
        coroutineJob.cancel()
//        EventBus.getDefault().unregister(this)
        unregisterReceiver(broadcastReceiver)
    }
}