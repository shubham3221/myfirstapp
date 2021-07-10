package com.example.myfirstapp.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.myfirstapp.Myconstants
import com.example.myfirstapp.R
import com.example.myfirstapp.googlesdk.GoogleActivity

// Notification ID.
private val FLAGS = 0

fun NotificationManager.showNotification(notificationID: Int, messageBody: String, applicationContext: Context) {
    // Create the content intent for the notification, which launches
    // this activity
    // TODO: Step 1.11 create intent
    val contentIntent = Intent(applicationContext, GoogleActivity::class.java)
    // TODO: Step 1.12 create PendingIntent
    val contentPendingIntent = PendingIntent.getActivity(applicationContext, notificationID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

//    // TODO: for adding big picture
//    val eggImage = BitmapFactory.decodeResource(
//        applicationContext.resources,
//        R.drawable.cooked_egg
//    )
//    val bigPicStyle = NotificationCompat.BigPictureStyle()
//        .bigPicture(eggImage)
//        .bigLargeIcon(null)

    // TODO: Step 2.2 add snooze action
    val snoozeIntent = Intent(applicationContext, MyBroadcastReceiver::class.java)
    val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        notificationID,
        snoozeIntent,
        FLAGS)

    // TODO: Step 1.2 get an instance of NotificationCompat.Builder
    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        Myconstants.CHANNAL_ID
    )

        // TODO: Step 1.3 set title, text and icon to builder
        .setSmallIcon(R.drawable.com_facebook_button_icon)
        .setContentTitle("my title")
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
//      .setStyle(bigPicStyle)
//      .setLargeIcon(eggImage)

        // TODO: Step 2.3 add button
        .addAction(
            R.drawable.com_facebook_button_icon,
            "button 1",
            snoozePendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    // TODO: Step 1.4 call notify
    notify(notificationID, builder.build())
}

fun NotificationManager.progressNotification(
    action: String,
    notificationID: Int,
    title: String,
    messageBody: String,
    applicationContext: Context,
) {
    val contentIntent = Intent(applicationContext, GoogleActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        notificationID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val action1 = Intent(applicationContext, MyBroadcastReceiver::class.java).apply {
        this.action = action
        this.putExtra("id", notificationID)
    }
    val action2 = Intent(applicationContext, MyBroadcastReceiver::class.java).apply {
        this.action = "Stop"
        this.putExtra("id", notificationID)
    }
    val action1_Intent: PendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        notificationID,
        action1,
        FLAGS)
    val action2_Intent: PendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        notificationID,
        action2,
        FLAGS)

    // TODO: Step 1.2 get an instance of NotificationCompat.Builder
    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        Myconstants.CHANNAL_ID
    )
        .setSmallIcon(R.drawable.com_facebook_button_icon)
        .setContentTitle(title)
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(
            R.drawable.com_facebook_button_icon,
            action,
            action1_Intent
        ).addAction(
            R.drawable.com_facebook_button_icon,
            "stop",
            action2_Intent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    // TODO: Step 1.4 call notify
    notify(notificationID, builder.build())
}

fun NotificationManager.updateNotification(
    action: String,
    notificationID: Int,
    title: String,
    messageBody: String,
    applicationContext: Context,
) {

}


// TODO: Step 1.14 Cancel all notifications
fun NotificationManager.cancelNotifications() {
    cancelAll()
}


