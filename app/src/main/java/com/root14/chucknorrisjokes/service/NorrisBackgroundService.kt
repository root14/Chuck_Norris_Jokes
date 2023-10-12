package com.root14.chucknorrisjokes.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.root14.chucknorrisjokes.MainActivity
import com.root14.chucknorrisjokes.R
import com.root14.chucknorrisjokes.data.database.repo.RoomRepository
import com.root14.chucknorrisjokes.data.network.RetrofitRepository
import com.root14.chucknorrisjokes.utils.NotificationParams
import com.root14.chucknorrisjokes.utils.PopNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class NorrisBackgroundService : Service() {

    //TODO: add to work manager to re-start service

    @Inject
    lateinit var retrofitRepository: RetrofitRepository

    @Inject
    lateinit var roomRepository: RoomRepository


    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        //create notification channel
        createNotificationChannel()
        //fetch categories
        ServiceController.initCategories(
            retrofitRepository = retrofitRepository, roomRepository = roomRepository
        )
        //fetch jokes
        ServiceController.fetchJokesByCategory(
            retrofitRepository = retrofitRepository, roomRepository = roomRepository
        )
        //TODO: handle no internet connection -> causes exception


        /*WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "unique_worker_name", ExistingPeriodicWorkPolicy.KEEP, //KEEP or REPLACE
            JokeWorker.getPeriodicWorkRequest() //your work instance.
        )*/

        WorkManager.getInstance(this).enqueue(JokeWorker.getOneTimeRequest())

        //super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
    }

    override fun startService(service: Intent?): ComponentName? {
        return super.startService(service)
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system.
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}