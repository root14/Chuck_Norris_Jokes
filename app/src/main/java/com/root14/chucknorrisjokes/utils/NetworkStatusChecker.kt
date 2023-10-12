package com.root14.chucknorrisjokes.utils

import android.content.Context
import android.net.ConnectivityManager


class NetworkStatusChecker() {

    fun checkConnection(context: Context): NetworkStatus {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return try {
            if (connectivityManager.activeNetworkInfo?.isConnected == true) {
                NetworkStatus.CONNECTED
            } else {
                NetworkStatus.NOT_CONNECTED
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            NetworkStatus.NOT_CONNECTED
        }
    }
}