package com.roko.kronos.processors.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.roko.kronos.MainActivity
import com.roko.kronos.MainApplication.Companion.applicationContext
import com.roko.kronos.R

object NotificationProcessor {

    private const val NOTIFICATION_CHANNEL_ID = "kronos_notification_channel_out_of_sync_alerts"
    private const val NOTIFICATION_GENERAL_ID = 1

    init {
        val channelName = applicationContext().getString(R.string.out_of_sync_alerts)
        val descriptionString = applicationContext().getString(R.string.out_of_sync_alerts_description_)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance).apply {
            description = descriptionString
        }
        (applicationContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            createNotificationChannel(channel)
        }
    }

    fun postNotification() {
        val intent = Intent(applicationContext(), MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(applicationContext(), NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_logo)
            .setContentTitle("Device clock out of sync")
            .setContentText("The device clock is 3 years and 4 seconds behind the network clock. Tap the notification to solve the abominable issue!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        if (ActivityCompat.checkSelfPermission(
                applicationContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(applicationContext())
                .notify(NOTIFICATION_GENERAL_ID, notification)
        } else {
            Toast.makeText(applicationContext(), "Hey, you must allow notifications for this app!", Toast.LENGTH_SHORT).show()
        }
    }

}
