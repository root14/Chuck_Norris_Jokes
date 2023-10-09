package com.root14.chucknorrisjokes

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.IBinder

class NorrisBackGroundWorker : Service() {

    //TODO: add to work manager to re-start service

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
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
}