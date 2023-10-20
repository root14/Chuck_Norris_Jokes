package com.root14.chucknorrisjokes.service

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.root14.chucknorrisjokes.R
import com.root14.chucknorrisjokes.utils.NetworkStatus
import com.root14.chucknorrisjokes.utils.NetworkStatusChecker
import com.root14.chucknorrisjokes.utils.NotificationParams
import com.root14.chucknorrisjokes.utils.PopNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class JokeWorker(
    context: Context,
    parameters: WorkerParameters,

    ) : CoroutineWorker(context, parameters) {

    private val _context: Context = context

    override suspend fun doWork(): Result {
        /**
         * -> internet connection -> provide random joke from api && check db for joke count and fill it!
         * -> internet connection & bad request & api exceptions -> provide from db
         * -> no internet connection -> provide from db
         */

        if (NetworkStatusChecker().checkConnection(_context) == NetworkStatus.CONNECTED) {
            println("norris made a joke from api!")


            /**
             * If norris is started without internet and the internet is connected while the service is running,
             * it fills the database.
             */
            if (ServiceController.initialized.value == false) {
                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    ServiceController.fetchJokesByCategory()
                }
            }

            ServiceController.getRandomJokeFromApi()
            ServiceController.jokeRandomJokeFromApi.observe(lifecycleOwner) {
                val notificationParams =
                    NotificationParams.Builder().setContentText(it.value.toString())
                        .setTitle(it.url.toString()).setContext(_context).build()

                PopNotification().popNotification(notificationParams)
            }
            return Result.success()

        } else if (NetworkStatusChecker().checkConnection(_context) == NetworkStatus.NOT_CONNECTED) {
            ServiceController.getJokeFromDb()
            ServiceController.jokeFromDb.observe(lifecycleOwner) {
                val notificationParams =
                    NotificationParams.Builder().setContentText(it.value.toString())
                        .setTitle(it.url.toString()).setContext(_context).build()

                PopNotification().popNotification(notificationParams)
            }

            return Result.success()
        }
        return Result.failure()
    }

    companion object {
        lateinit var lifecycleOwner: LifecycleOwner

        fun getPeriodicWorkRequest(): PeriodicWorkRequest {

            val constraints = Constraints.Builder()
                //.setRequiredNetworkType(NetworkType.CONNECTED) //worker will run when Network is Connected.
                .build()

            return PeriodicWorkRequestBuilder<JokeWorker>(
                3, //every 15 Minutes, this worker will run.It should be Greater then or equal to 15 minutes.
                TimeUnit.HOURS
            ).setConstraints(constraints).build()
        }

        fun getOneTimeRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<JokeWorker>().build()
        }

    }
}
