package com.root14.chucknorrisjokes.utils

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.root14.chucknorrisjokes.MainActivity
import com.root14.chucknorrisjokes.R

class PopNotification {
    fun popNotification(notificationParams: NotificationParams) {
        val intent = Intent(notificationParams.context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(
                notificationParams.context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        //.setContentTitle(notificationParams.title)
        val builder = NotificationCompat.Builder(notificationParams.context!!, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_foreground)

            .setContentText(notificationParams.contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle())
            // Set the intent that fires when the user taps the notification.
            .setContentIntent(pendingIntent).setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(
                notificationParams.context!!, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(notificationParams.context!!)) {
                // notificationId is a unique int for each notification that you must define.
                notify(R.string.notificationId, builder.build())
            }
        }
    }
}