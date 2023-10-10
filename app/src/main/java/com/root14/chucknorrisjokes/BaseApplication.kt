package com.root14.chucknorrisjokes

import android.app.Application
import androidx.room.Room
import com.root14.chucknorrisjokes.data.database.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {
}