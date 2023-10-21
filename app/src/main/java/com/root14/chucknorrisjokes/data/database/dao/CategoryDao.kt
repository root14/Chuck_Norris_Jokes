package com.root14.chucknorrisjokes.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.root14.chucknorrisjokes.data.database.entity.CategoryEntity

@Dao
interface CategoryDao {
    @Query("SELECT * FROM CategoryEntity")
    fun getAll(): List<CategoryEntity>

    @Insert
    suspend fun insertAll(vararg category: CategoryEntity)

    @Insert
    suspend fun insert(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)

    @Query("DELETE FROM CategoryEntity")
    fun deleteAll()
}