package com.podolyanchik.hockeyshop.data.repository

import com.podolyanchik.hockeyshop.data.local.dao.CategoryDao
import com.podolyanchik.hockeyshop.data.local.entity.CategoryEntity
import com.podolyanchik.hockeyshop.domain.model.Category
import com.podolyanchik.hockeyshop.domain.repository.CategoryRepository
import com.podolyanchik.hockeyshop.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    
    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toCategory() }
        }
    }
    
    override suspend fun getCategoryById(id: Int): Resource<Category> {
        return try {
            val categoryEntity = categoryDao.getCategoryById(id) 
                ?: return Resource.Error("Категория не найдена")
            
            Resource.Success(categoryEntity.toCategory())
        } catch (e: Exception) {
            Resource.Error("Ошибка при получении категории: ${e.localizedMessage}")
        }
    }
    
    override fun searchCategories(query: String): Flow<List<Category>> {
        return categoryDao.searchCategories(query).map { entities ->
            entities.map { it.toCategory() }
        }
    }
    
    override suspend fun addCategory(category: Category): Resource<Unit> {
        return try {
            val categoryEntity = CategoryEntity(
                id = category.id,
                name = category.name,
                iconUrl = category.iconUrl,
                description = category.description
            )
            
            categoryDao.insertCategory(categoryEntity)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Ошибка при добавлении категории: ${e.localizedMessage}")
        }
    }
    
    override suspend fun updateCategory(category: Category): Resource<Unit> {
        return try {
            val existingCategory = categoryDao.getCategoryById(category.id)
                ?: return Resource.Error("Категория не найдена")
            
            val updatedCategory = existingCategory.copy(
                name = category.name,
                iconUrl = category.iconUrl,
                description = category.description
            )
            
            categoryDao.updateCategory(updatedCategory)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Ошибка при обновлении категории: ${e.localizedMessage}")
        }
    }
    
    override suspend fun deleteCategory(id: Int): Resource<Unit> {
        return try {
            val category = categoryDao.getCategoryById(id)
                ?: return Resource.Error("Категория не найдена")
            
            categoryDao.deleteCategory(category)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Ошибка при удалении категории: ${e.localizedMessage}")
        }
    }
    
    private fun CategoryEntity.toCategory(): Category {
        return Category(
            id = id,
            name = name,
            iconUrl = iconUrl,
            description = description
        )
    }
} 