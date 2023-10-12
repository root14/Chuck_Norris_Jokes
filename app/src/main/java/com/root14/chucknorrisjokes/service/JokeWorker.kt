package com.root14.chucknorrisjokes.service

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.root14.chucknorrisjokes.utils.NetworkStatus
import com.root14.chucknorrisjokes.utils.NetworkStatusChecker
import java.util.concurrent.TimeUnit


class JokeWorker(
    context: Context,
    parameters: WorkerParameters,

    ) : CoroutineWorker(context, parameters) {

    private val _context: Context = context

    override suspend fun doWork(): Result {
        //internet connection -> provide random joke from api && check db for joke count and fill it!
        //internet connection & bad request & api exceptions -> provide from db
        //no internet connection -> provide from db


        if (NetworkStatusChecker().checkConnection(_context) == NetworkStatus.CONNECTED) {
            println("norris made a joke from api!")
            return Result.success()

        } else if (NetworkStatusChecker().checkConnection(_context) == NetworkStatus.NOT_CONNECTED) {
            return Result.failure()
        }
        return Result.failure()
    }

    companion object {
        fun getPeriodicWorkRequest(): PeriodicWorkRequest {

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) //worker will run,Network is Connected.
                .build()

            return PeriodicWorkRequestBuilder<JokeWorker>(
                15, //every 15 Minutes, this worker will run.It should be Greater then or equal to 15 minutes.
                TimeUnit.MINUTES
            ).setConstraints(constraints).build()
        }

        fun getOneTimeRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<JokeWorker>().build()
        }

    }
}
