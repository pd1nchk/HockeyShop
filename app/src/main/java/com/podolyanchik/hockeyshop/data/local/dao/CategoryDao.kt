package com.podolyanchik.hockeyshop.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.podolyanchik.hockeyshop.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)
    
    @Update
    suspend fun updateCategory(category: CategoryEntity)
    
    @Delete
    suspend fun deleteCategory(category: CategoryEntity)
    
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>
    
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Int): CategoryEntity?
    
    @Query("SELECT * FROM categories WHERE name LIKE '%' || :query || '%'")
    fun searchCategories(query: String): Flow<List<CategoryEntity>>
} 