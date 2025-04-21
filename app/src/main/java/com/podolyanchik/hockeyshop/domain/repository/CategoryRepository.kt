package com.podolyanchik.hockeyshop.domain.repository

import com.podolyanchik.hockeyshop.domain.model.Category
import com.podolyanchik.hockeyshop.util.Resource
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    
    fun getAllCategories(): Flow<List<Category>>
    
    suspend fun getCategoryById(id: Int): Resource<Category>
    
    fun searchCategories(query: String): Flow<List<Category>>
    
    suspend fun addCategory(category: Category): Resource<Unit>
    
    suspend fun updateCategory(category: Category): Resource<Unit>
    
    suspend fun deleteCategory(id: Int): Resource<Unit>
} 