package com.root14.chucknorrisjokes.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.root14.chucknorrisjokes.data.database.repo.RoomRepository
import com.root14.chucknorrisjokes.data.network.RetrofitRepository
import com.root14.chucknorrisjokes.model.JokeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ServiceController {
    companion object {


        private val _joke = MutableLiveData<JokeModel>()
        val joke: LiveData<JokeModel> = _joke
        fun getJoke(retrofitRepository: RetrofitRepository, roomRepository: RoomRepository) {
            CoroutineScope(Dispatchers.IO).launch {
                val data = roomRepository.getJoke()
                if (data != null) {
                    _joke.postValue(
                        JokeModel(
                            iconUrl = data.iconUrl,
                            id = data.id,
                            url = data.url,
                            value = data.value,
                        )
                    )
                } else {//if cannot get any joke from db
                    val data0 = retrofitRepository.getRandomJoke()
                    if (data0.isSuccessful) {
                        _joke.postValue(data0.body())
                    }
                }

            }


        }

        private val _categorysCheck = MutableLiveData<Boolean>()
        val categorysCheck: LiveData<Boolean> = _categorysCheck
        fun initCategories(
            retrofitRepository: RetrofitRepository, roomRepository: RoomRepository
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                val categories = retrofitRepository.getCategories() //get from api

                if (categories.isSuccessful) {
                    categories.body()
                        ?.let { _categories -> roomRepository.saveCategory(_categories) }
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
         * @param retrofitRepository for api calls
         * @param roomRepository for db calls
         * @return save db jokes on every category on every called
         */
        fun fetchJokesByCategory(
            retrofitRepository: RetrofitRepository, roomRepository: RoomRepository
        ) {
            CoroutineScope(Dispatchers.IO).launch {
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
        }
    }
}