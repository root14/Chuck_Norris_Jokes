package com.root14.chucknorrisjokes.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.lifecycle.LifecycleService
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.root14.chucknorrisjokes.R
import com.root14.chucknorrisjokes.data.database.repo.RoomRepository
import com.root14.chucknorrisjokes.data.network.RetrofitRepository
import com.root14.chucknorrisjokes.utils.NetworkStatus
import com.root14.chucknorrisjokes.utils.NetworkStatusChecker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class NorrisBackgroundService : LifecycleService() {
    @Inject
    lateinit var retrofitRepository: RetrofitRepository

    @Inject
    lateinit var roomRepository: RoomRepository


    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        //inject room repository
        ServiceController.injectRoomRepository(roomRepository)

        //create notification channel
        createNotificationChannel()

        if (NetworkStatusChecker().checkConnection(this) == NetworkStatus.CONNECTED) {
            //inject retrofit repository
            ServiceController.injectRetrofitRepository(retrofitRepository)

            //fetch categories
            ServiceController.initCategories()
            //fetch jokes
            ServiceController.fetchJokesByCategory()
        }

        JokeWorker.lifecycleOwner = this

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "unique_worker_name", ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, //KEEP or REPLACE
            JokeWorker.getPeriodicWorkRequest()
        )

        WorkManager.getInstance(this).enqueue(JokeWorker.getOneTimeRequest())

        return START_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        Toast.makeText(this, "norris service stopped.", Toast.LENGTH_SHORT).show()
        return super.stopService(name)
    }

    override fun startService(service: Intent?): ComponentName? {
        Toast.makeText(this, "norris service started.", Toast.LENGTH_SHORT).show()
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