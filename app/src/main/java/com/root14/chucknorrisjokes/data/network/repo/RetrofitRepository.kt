package com.root14.chucknorrisjokes.data.network.repo

import com.root14.chucknorrisjokes.data.network.service.RetrofitService

class RetrofitRepository(private val retrofitService: RetrofitService) {
    suspend fun getRandomJoke() = retrofitService.getRandomJoke()
    suspend fun getRandomJokesByCategory(category: String) =
        retrofitService.getRandomJokesByCategory(category)

    suspend fun getCategories() = retrofitService.getCategories()
}