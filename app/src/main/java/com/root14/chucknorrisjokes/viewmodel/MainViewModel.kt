package com.root14.chucknorrisjokes.viewmodel

import android.os.Debug
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.root14.chucknorrisjokes.data.database.repo.RoomRepository
import com.root14.chucknorrisjokes.data.network.RetrofitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val retrofitRepository: RetrofitRepository, private val roomRepository: RoomRepository
) : ViewModel() {

    private val _categorysCheck = MutableLiveData<Boolean>()
    val categorysCheck: LiveData<Boolean> = _categorysCheck

    fun initCategories() {
        //every launch get data to keep categories updated
        viewModelScope.launch(Dispatchers.IO) {
            val categories = retrofitRepository.getCategories() //get from api

            if (categories.isSuccessful) {
                categories.body()?.let { _categories -> roomRepository.saveCategory(_categories) }
                _categorysCheck.postValue(true)
            } else {//if category request failed try to get from db old data
                //TODO:error handling

                val categoriesDb = roomRepository.getCategories()
                //set boolean to _categorysCheck is categories in db
                _categorysCheck.postValue(categoriesDb.isNotEmpty())
            }
        }
    }


    private val _jokeCheck = MutableLiveData<Boolean>()
    val jokeCheck: LiveData<Boolean> = _jokeCheck

    /**
     * @return save db jokes on every category on every time called
     */
    fun getJokesByCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            val category = roomRepository.getCategories() //get category from db
            if (category.isNotEmpty()) {
                try {
                    category.forEach { _category ->
                        val data =
                            retrofitRepository.getRandomJokesByCategory(_category.categoryName)//get random jokes by category

                        if (data.isSuccessful) {
                            data.body()?.let { roomRepository.saveJoke(it) }//save to db
                        }
                    }
                    _jokeCheck.postValue(true)
                } catch (e: Exception) {
                    //TODO: TEST ME
                    //if any problem with endpoint
                    var categoryCount = 16 //static size for bad scenario
                    while (categoryCount > 0) {
                        val randomJoke = retrofitRepository.getRandomJoke()
                        if (randomJoke.isSuccessful) {
                            randomJoke.body()?.let { roomRepository.saveJoke(it) }
                            _jokeCheck.postValue(true)
                        } else {
                            //TODO:error handle
                            e.message?.let { Log.e("getJokesByCategory-exception", it) }
                            _jokeCheck.postValue(false)//err
                        }
                        categoryCount--
                    }
                }


            }
        }
        println("hey douglas! this is check-point. debug your jokes!")
    }

    /**
     * @return random joke
     * random joke method used as an emergency exit when there is a problem at the category endpoint
     */
    suspend fun getRandomJoke() = retrofitRepository.getRandomJoke()


    suspend fun printData() {
        Log.d("category", roomRepository.getCategories().toString())
        Log.d("jokes", roomRepository.getJoke().toString())
    }
}