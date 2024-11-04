package com.root14.chucknorrisjokes

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.root14.chucknorrisjokes.data.database.repo.RoomRepository
import com.root14.chucknorrisjokes.data.network.repo.RetrofitRepository
import com.root14.chucknorrisjokes.databinding.ActivityMainBinding
import com.root14.chucknorrisjokes.data.background.NorrisBackgroundService
import com.root14.chucknorrisjokes.controller.ServiceController
import com.root14.chucknorrisjokes.utils.AdHelper
import com.root14.chucknorrisjokes.utils.NetworkStatus
import com.root14.chucknorrisjokes.utils.NetworkStatusChecker
import com.root14.chucknorrisjokes.utils.NotificationParams
import com.root14.chucknorrisjokes.utils.PopNotification
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


    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * required steps for startup
         */
        prepUi()
        checkPermission()
        ignoreBatteryOptimization()

        /**
         * handle permissions
         */
        ServiceController.notificationPermission.observe(this) { permission ->
            if (permission) {
                if (isIgnoringBatteryOptimizations()) {
                    //#454545
                    when (NetworkStatusChecker().checkConnection(this)) {
                        NetworkStatus.CONNECTED -> {
                            ServiceController._batteryOptimization.postValue(true)
                            changeJokeButtonAvailability(true)
                            val serviceIntent = Intent(this, NorrisBackgroundService::class.java)
                            //start services
                            // startService(serviceIntent)
                        }

                        NetworkStatus.NOT_CONNECTED -> {
                            changeJokeButtonAvailability(false)
                            Toast.makeText(
                                this,
                                "Unable to access the internet. Restart the application once access is granted.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

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
            } else {
                val serviceIntent = Intent(this, NorrisBackgroundService::class.java)
                startService(serviceIntent)
            }
        }

        /**
         * handle get joke button
         */
        binding.btnPopJoke.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                if (NetworkStatusChecker().checkConnection(this@MainActivity) == NetworkStatus.CONNECTED) {
                    ServiceController.getRandomJokeFromApi()
                    lifecycleScope.launch(Dispatchers.Main) {
                        AdHelper.awardedAd(this@MainActivity).let { ad ->
                            ad?.show(this@MainActivity, OnUserEarnedRewardListener {
                                Log.d("norris ad", "User earned the reward.")

                                ServiceController.jokeRandomJokeFromApi.observe(this@MainActivity) {
                                    val notificationParams =
                                        NotificationParams.Builder().setContentText(it.value.toString())
                                            .setTitle(it.url.toString()).setContext(this@MainActivity).build()

                                    PopNotification().popNotification(notificationParams)
                                }

                            })
                        } ?: run {
                            Log.d("norris ad", "The rewarded ad wasn't ready yet.")
                        }
                    }


                } else if (NetworkStatusChecker().checkConnection(this@MainActivity) == NetworkStatus.NOT_CONNECTED) {
                    if (roomRepository.getJokeCount() >= 0) {
                        ServiceController.getJokeFromDb()
                    } else {
                        Toast.makeText(
                            this@MainActivity, "cannot provide joke now.", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        ServiceController.jokeFromDb.observe(this@MainActivity) {
            val notificationParams =
                NotificationParams.Builder().setContentText(it.value.toString())
                    .setTitle(it.url.toString()).setContext(this@MainActivity).build()
            PopNotification().popNotification(notificationParams)
        }


        /**
         * handle social buttons
         */
        binding.imageButtonGithub.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/rabaduptis"))
            startActivity(browserIntent)
        }

        binding.imageButtonLinkedin.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.linkedin.com/in/basri-ilkay-gavaz/")
            )
            startActivity(browserIntent)
        }
    }

    private fun changeJokeButtonAvailability(boolean: Boolean) {
        ServiceController._networkAvailability.postValue(boolean)
        binding.btnPopJoke.isClickable = boolean

        binding.btnPopJoke.setBackgroundColor(
            if (boolean) Color.parseColor("#1E91EF") else Color.parseColor(
                "#454545"
            )
        )
    }

    private fun prepUi() {
        val bitmapGithub = BitmapFactory.decodeResource(
            resources, R.mipmap.social_github
        )
        binding.imageButtonGithub.setImageBitmap(bitmapGithub)

        val bitmapLinkedin = BitmapFactory.decodeResource(
            resources, R.mipmap.social_linkedin
        )
        binding.imageButtonLinkedin.setImageBitmap(bitmapLinkedin)

        binding.twContribute.movementMethod = LinkMovementMethod.getInstance()
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

    @SuppressLint("BatteryLife")
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