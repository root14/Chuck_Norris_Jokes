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
import java.util.concurrent.TimeUnit

class JokeWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {

        //there is gonna work if network connected, so we can provide random joke.
        // with this way we not gonna waste joke in db


        return try {
            Log.d("worker", "in every 15 seconds")
            Result.success()
        } catch (e: Exception) {
            Log.e("worker_exception", e.message.toString())
            Result.failure()
        }
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
