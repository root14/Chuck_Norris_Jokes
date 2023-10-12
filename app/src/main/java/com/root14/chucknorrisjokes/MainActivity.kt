package com.root14.chucknorrisjokes

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.root14.chucknorrisjokes.data.database.repo.RoomRepository
import com.root14.chucknorrisjokes.service.JokeWorker
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()

        ServiceController.internetPermission.observe(this) { permission ->
            if (permission) {
                val serviceIntent = Intent(this, NorrisBackgroundService::class.java)
                startService(serviceIntent)
                Toast.makeText(this, "norris service started.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //granted -> 0
        //not granted -> -1
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            // PERMISSION_GRANTED
            ServiceController._internetPermission.postValue(true)
        } else {
            // PERMISSION_NOT_GRANTED
            Toast.makeText(this, "pls grand permissions!", Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.Default).launch {
                delay(3500)
                ServiceController._internetPermission.postValue(false)
                checkPermission()
            }
        }
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                baseContext, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 42
            )
        }
    }
}