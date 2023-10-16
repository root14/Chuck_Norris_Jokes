package com.root14.chucknorrisjokes.data.database.repo

import com.root14.chucknorrisjokes.data.database.dao.CategoryDao
import com.root14.chucknorrisjokes.data.database.dao.JokeDao
import com.root14.chucknorrisjokes.data.database.entity.CategoryEntity
import com.root14.chucknorrisjokes.data.database.entity.JokeEntity
import com.root14.chucknorrisjokes.model.JokeModel

class RoomRepository(private val categoryDao: CategoryDao, private val jokeDao: JokeDao) {


    suspend fun getJokeCount(): Int {
        return jokeDao.getCount()
    }

    suspend fun saveCategory(category: List<String>) {

        val categoryDb = categoryDao.getAll()
        val dummyList = arrayListOf<String>()

        categoryDb.forEach {
            dummyList.add(it.categoryName)
        }

        if (!category.equals(dummyList))
            category.forEach {
                categoryDao.insert(CategoryEntity(categoryName = it))
            }
    }

    suspend fun saveJoke(jokeModel: JokeModel) {
        jokeDao.insert(
            JokeEntity(
                iconUrl = jokeModel.iconUrl.toString(),
                id = jokeModel.id.toString(),
                url = jokeModel.url.toString(),
                value = jokeModel.value.toString()
            )
        )
    }

    suspend fun getJoke(): JokeEntity? {
        return try {
            val data = jokeDao.getJoke()
            //jokeDao.delete(data)
            data
        } catch (exception: Exception) {
            null
        }
    }

    suspend fun getAllJokes(): List<JokeEntity> {
        return jokeDao.getAll()
    }

    /**
     * @return all categories
     */
    fun getCategories() = categoryDao.getAll()

}