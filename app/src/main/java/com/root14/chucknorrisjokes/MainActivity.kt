package com.root14.chucknorrisjokes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.root14.chucknorrisjokes.data.database.repo.RoomRepository
import com.root14.chucknorrisjokes.data.network.RetrofitRepository
import com.root14.chucknorrisjokes.service.NorrisBackgroundService
import com.root14.chucknorrisjokes.service.ServiceController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var roomRepository: RoomRepository

    @Inject
    lateinit var retrofitRepository: RetrofitRepository

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()
        ignoreBatteryOptimization()

        ServiceController.notificationPermission.observe(this) { permission ->
            if (permission) {
                if (isIgnoringBatteryOptimizations()) {
                    ServiceController._batteryOptimization.postValue(true)
                    val serviceIntent = Intent(this, NorrisBackgroundService::class.java)
                    startService(serviceIntent)
                } else {
                    Toast.makeText(
                        this, "please give battery optimization permission.", Toast.LENGTH_SHORT
                    ).show()
                    ServiceController._batteryOptimization.postValue(false)
                }
            }
        }

        ServiceController.batteryOptimization.observe(this) { optimization ->
            if (!optimization) {
                CoroutineScope(Dispatchers.Default).launch {
                    delay(5000)
                    ignoreBatteryOptimization()
                }
            }else{
                val serviceIntent = Intent(this, NorrisBackgroundService::class.java)
                startService(serviceIntent)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //granted -> 0
        //not granted -> -1
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            // PERMISSION_GRANTED
            ServiceController._notificationPermission.postValue(true)
        } else {
            // PERMISSION_NOT_GRANTED
            Toast.makeText(this, "pls grand permissions!", Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.Default).launch {
                delay(3500)
                ServiceController._notificationPermission.postValue(false)
                checkPermission()
            }
        }
    }

    private fun isIgnoringBatteryOptimizations(): Boolean {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(packageName)
    }

    private fun ignoreBatteryOptimization() {
        val intent = Intent()
        val packageName = packageName
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.setData(Uri.parse("package:$packageName"))
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                baseContext, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ServiceController._notificationPermission.postValue(false)
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 42
            )
        } else {
            ServiceController._notificationPermission.postValue(true)
        }
    }
}