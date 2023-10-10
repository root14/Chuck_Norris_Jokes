package com.root14.chucknorrisjokes.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.root14.chucknorrisjokes.data.database.dao.CategoryDao
import com.root14.chucknorrisjokes.data.database.dao.JokeDao
import com.root14.chucknorrisjokes.data.database.entity.CategoryEntity
import com.root14.chucknorrisjokes.data.database.entity.JokeEntity

@Database(entities = [CategoryEntity::class, JokeEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jokeDao(): JokeDao
    abstract fun categoryDao(): CategoryDao
}