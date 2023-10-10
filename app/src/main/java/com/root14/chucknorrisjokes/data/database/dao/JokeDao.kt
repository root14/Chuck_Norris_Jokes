package com.root14.chucknorrisjokes.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.root14.chucknorrisjokes.data.database.entity.JokeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JokeDao {
    @Query("SELECT * FROM JokeEntity")
    fun getAll():List<JokeEntity>

    @Query("SELECT * FROM JokeEntity ORDER BY RANDOM() LIMIT 1")
    suspend fun getJoke(): JokeEntity

    @Insert
    suspend fun insertAll(vararg jokeEntity: JokeEntity)

    @Insert
    suspend fun insert(jokeEntity: JokeEntity)

    @Delete
    suspend fun delete(jokeEntity: JokeEntity)
}