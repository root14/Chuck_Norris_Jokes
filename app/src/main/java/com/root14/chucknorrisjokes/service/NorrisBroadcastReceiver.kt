package com.root14.chucknorrisjokes.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NorrisBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        //todo: when reboot re-start service again

        //todo: when network connected check joke count in db, if its needed fill it.
        //todo: when network disconnect, cancel enqueue of work manager and set periodicWorkRequest but provide from db
    }
}