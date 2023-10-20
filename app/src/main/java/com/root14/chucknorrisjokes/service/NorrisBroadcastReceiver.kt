package com.root14.chucknorrisjokes.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NorrisBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
            val serviceIntent = Intent(context, NorrisBackgroundService::class.java)
            context?.startService(serviceIntent)
        }
    }
}