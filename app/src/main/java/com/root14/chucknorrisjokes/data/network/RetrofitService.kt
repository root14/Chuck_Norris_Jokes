package com.root14.chucknorrisjokes.data.network

import com.root14.chucknorrisjokes.data.Result
import com.root14.chucknorrisjokes.model.JokeModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    @GET("random")
    suspend fun getRandomJoke(): Response<JokeModel>

    @GET("categories")
    suspend fun getCategories(): Response<List<String>>

    @GET("random")
    suspend fun getRandomJokesByCategory(@Query("category") category: String): Response<JokeModel>
}