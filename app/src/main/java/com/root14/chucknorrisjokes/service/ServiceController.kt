package com.root14.chucknorrisjokes.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.root14.chucknorrisjokes.data.database.entity.JokeEntity
import com.root14.chucknorrisjokes.data.database.repo.RoomRepository
import com.root14.chucknorrisjokes.data.network.RetrofitRepository
import com.root14.chucknorrisjokes.model.JokeModel
import com.root14.chucknorrisjokes.utils.NetworkStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ServiceController {


    companion object {
        var _internetPermission = MutableLiveData<Boolean>()
        var internetPermission: LiveData<Boolean> = _internetPermission

        /**
         * @param roomRepository
         * @return joke count in room db
         * @exception at any problem return = -1
         */
        fun getJokeCountInDb(roomRepository: RoomRepository): Int {
            var count: Int = -1
            CoroutineScope(Dispatchers.IO).launch {
                count = roomRepository.getJokeCount()
            }
            return count
        }


        private val _jokeRandomJokeFromApi = MutableLiveData<JokeModel>()
        val jokeRandomJokeFromApi: LiveData<JokeModel> = _jokeRandomJokeFromApi

        /**
         * @param retrofitRepository
         * @return provide random joke from api
         */
        fun getRandomJokeFromApi(retrofitRepository: RetrofitRepository) {
            CoroutineScope(Dispatchers.IO).launch {

                val data = retrofitRepository.getRandomJoke()
                if (data.isSuccessful) {
                    _jokeRandomJokeFromApi.postValue(data.body())
                } else {
                    Log.e("random joke", "cannot provide random joke!")
                }
            }
        }

        /**
         * @param roomRepository roomDB repo
         * @return joke from db
         */
        private val _jokeFromDb = MutableLiveData<JokeModel>()
        val jokeFromDb: LiveData<JokeModel> = _jokeFromDb
        fun getJokeFromDb(roomRepository: RoomRepository) {
            CoroutineScope(Dispatchers.IO).launch {
                val data = roomRepository.getJoke()
                if (data != null) {
                    _jokeFromDb.postValue(
                        JokeModel(
                            iconUrl = data.iconUrl,
                            id = data.id,
                            url = data.url,
                            value = data.value,
                        )
                    )
                } else {
                    Log.e("joke from db", "cannot provide joke from db!")
                }
            }
        }


        private val _joke = MutableLiveData<JokeModel>()
        val joke: LiveData<JokeModel> = _joke

        /**
         * @param roomRepository
         * @param retrofitRepository
         * @return this method try to automize process. be carefully when use it
         * @return try to provide firstly from room db if any problem occurs return random joke from api
         */
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

        //initialise method
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