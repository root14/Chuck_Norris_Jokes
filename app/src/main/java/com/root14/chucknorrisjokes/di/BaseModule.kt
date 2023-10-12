package com.root14.chucknorrisjokes.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.root14.chucknorrisjokes.data.database.AppDatabase
import com.root14.chucknorrisjokes.data.database.dao.CategoryDao
import com.root14.chucknorrisjokes.data.database.dao.JokeDao
import com.root14.chucknorrisjokes.data.database.repo.RoomRepository
import com.root14.chucknorrisjokes.data.network.RetrofitRepository
import com.root14.chucknorrisjokes.data.network.RetrofitService
import com.root14.chucknorrisjokes.utils.NetworkStatusChecker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BaseModule {

    private const val BASE_URL = "https://api.chucknorris.io/jokes/"

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL)
            .client(okHttpClient).build()


    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): RetrofitService =
        retrofit.create(RetrofitService::class.java)

    @Singleton
    @Provides
    fun providesRetrofitRepository(apiService: RetrofitService) = RetrofitRepository(apiService)

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application, AppDatabase::class.java, "database-norris"
        ).build()
    }

    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao {
        return appDatabase.categoryDao()
    }

    @Provides
    fun provideJokeDao(appDatabase: AppDatabase): JokeDao {
        return appDatabase.jokeDao()
    }

    @Singleton
    @Provides
    fun providesRoomRepository(categoryDao: CategoryDao, jokeDao: JokeDao) =
        RoomRepository(categoryDao = categoryDao, jokeDao = jokeDao)

}