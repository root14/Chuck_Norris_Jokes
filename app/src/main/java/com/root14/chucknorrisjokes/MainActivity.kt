package com.root14.chucknorrisjokes

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Insert
import com.root14.chucknorrisjokes.data.database.repo.RoomRepository
import com.root14.chucknorrisjokes.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var roomRepository: RoomRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serviceIntent = Intent(this, NorrisBackGroundWorker::class.java)
        //startService(serviceIntent)

        mainViewModel.initCategories()

        mainViewModel.categorysCheck.observe(this) {
            if (it) {
                mainViewModel.getJokesByCategory()
            }
        }

        mainViewModel.jokeCheck.observe(this) {
            mainViewModel.viewModelScope.launch(Dispatchers.IO) {
                if (it) {
                    roomRepository
                }
            }
        }

    }
}