package com.podolyanchik.hockeyshop.data.repository

import com.podolyanchik.hockeyshop.data.local.dao.CategoryDao
import com.podolyanchik.hockeyshop.data.local.dao.ProductDao
import com.podolyanchik.hockeyshop.data.local.entity.ProductEntity
import com.podolyanchik.hockeyshop.domain.model.Category
import com.podolyanchik.hockeyshop.domain.model.Product
import com.podolyanchik.hockeyshop.domain.repository.ProductRepository
import com.podolyanchik.hockeyshop.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao
) : ProductRepository {
    
    override fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts().map { productEntities ->
            productEntities.mapNotNull { productEntity ->
                val category = categoryDao.getCategoryById(productEntity.categoryId) ?: return@mapNotNull null
                productEntity.toProduct(category.toCategory())
            }
        }
    }
    
    override suspend fun getProductById(id: String): Resource<Product> {
        return try {
            val productEntity = productDao.getProductById(id)
                ?: return Resource.Error("Товар не найден")
                
            val categoryEntity = categoryDao.getCategoryById(productEntity.categoryId)
                ?: return Resource.Error("Категория товара не найдена")
                
            val product = productEntity.toProduct(categoryEntity.toCategory())
            Resource.Success(product)
        } catch (e: Exception) {
            Resource.Error("Ошибка при получении товара: ${e.localizedMessage}")
        }
    }
    
    override fun observeProductById(id: String): Flow<Product?> {
        return productDao.observeProductById(id).map { productEntity ->
            productEntity?.let {
                val categoryEntity = categoryDao.getCategoryById(it.categoryId) ?: return@map null
                it.toProduct(categoryEntity.toCategory())
            }
        }
    }
    
    override fun getProductsByCategory(categoryId: Int): Flow<List<Product>> {
        return productDao.getProductsByCategory(categoryId).map { productEntities ->
            val category = categoryDao.getCategoryById(categoryId)?.toCategory()
                ?: return@map emptyList()
                
            productEntities.map { it.toProduct(category) }
        }
    }
    
    override fun searchProducts(query: String): Flow<List<Product>> {
        return productDao.searchProducts(query).map { productEntities ->
            productEntities.mapNotNull { productEntity ->
                val category = categoryDao.getCategoryById(productEntity.categoryId) ?: return@mapNotNull null
                productEntity.toProduct(category.toCategory())
            }
        }
    }
    
    override fun getSortedProductsByPrice(ascending: Boolean): Flow<List<Product>> {
        return (if (ascending) {
            productDao.getProductsOrderedByPriceAsc()
        } else {
            productDao.getProductsOrderedByPriceDesc()
        }).map { productEntities ->
            productEntities.mapNotNull { productEntity ->
                val category = categoryDao.getCategoryById(productEntity.categoryId) ?: return@mapNotNull null
                productEntity.toProduct(category.toCategory())
            }
        }
    }
    
    override fun getProductsByPopularity(): Flow<List<Product>> {
        return productDao.getProductsOrderedByRating().map { productEntities ->
            productEntities.mapNotNull { productEntity ->
                val category = categoryDao.getCategoryById(productEntity.categoryId) ?: return@mapNotNull null
                productEntity.toProduct(category.toCategory())
            }
        }
    }
    
    override fun getNewProducts(): Flow<List<Product>> {
        return productDao.getNewProducts().map { productEntities ->
            productEntities.mapNotNull { productEntity ->
                val category = categoryDao.getCategoryById(productEntity.categoryId) ?: return@mapNotNull null
                productEntity.toProduct(category.toCategory())
            }
        }
    }
    
    override fun getPopularProducts(): Flow<List<Product>> {
        return productDao.getPopularProducts().map { productEntities ->
            productEntities.mapNotNull { productEntity ->
                val category = categoryDao.getCategoryById(productEntity.categoryId) ?: return@mapNotNull null
                productEntity.toProduct(category.toCategory())
            }
        }
    }
    
    override suspend fun addProduct(product: Product): Resource<Unit> {
        return try {
            val productId = product.id.ifEmpty { UUID.randomUUID().toString() }
            
            val productEntity = ProductEntity(
                id = productId,
                name = product.name,
                description = product.description,
                price = product.price,
                imageUrl = product.imageUrl,
                categoryId = product.category.id,
                quantity = product.quantity,
                rating = product.rating,
                discount = product.discount,
                additionalImages = product.additionalImages,
                isPopular = product.isPopular,
                isNew = product.isNew
            )
            
            productDao.insertProduct(productEntity)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Ошибка при добавлении товара: ${e.localizedMessage}")
        }
    }
    
    override suspend fun updateProduct(product: Product): Resource<Unit> {
        return try {
            val existingProduct = productDao.getProductById(product.id)
                ?: return Resource.Error("Товар не найден")
                
            val updatedProduct = existingProduct.copy(
                name = product.name,
                description = product.description,
                price = product.price,
                imageUrl = product.imageUrl,
                categoryId = product.category.id,
                quantity = product.quantity,
                rating = product.rating,
                discount = product.discount,
                additionalImages = product.additionalImages,
                isPopular = product.isPopular,
                isNew = product.isNew
            )
            
            productDao.updateProduct(updatedProduct)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Ошибка при обновлении товара: ${e.localizedMessage}")
        }
    }
    
    override suspend fun deleteProduct(id: String): Resource<Unit> {
        return try {
            val product = productDao.getProductById(id)
                ?: return Resource.Error("Товар не найден")
                
            productDao.deleteProduct(product)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Ошибка при удалении товара: ${e.localizedMessage}")
        }
    }
    
    override suspend fun decreaseProductQuantity(id: String, amount: Int): Resource<Unit> {
        return try {
            val product = productDao.getProductById(id)
                ?: return Resource.Error("Товар не найден")
                
            if (product.quantity < amount) {
                return Resource.Error("Недостаточное количество товара на складе")
            }
            
            productDao.decreaseProductQuantity(id, amount)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Ошибка при уменьшении количества товара: ${e.localizedMessage}")
        }
    }
    
    override suspend fun increaseProductQuantity(id: String, amount: Int): Resource<Unit> {
        return try {
            val product = productDao.getProductById(id)
                ?: return Resource.Error("Товар не найден")
                
            productDao.increaseProductQuantity(id, amount)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Ошибка при увеличении количества товара: ${e.localizedMessage}")
        }
    }
    
    private fun ProductEntity.toProduct(category: Category): Product {
        return Product(
            id = id,
            name = name,
            description = description,
            price = price,
            imageUrl = imageUrl,
            category = category,
            quantity = quantity,
            rating = rating,
            discount = discount,
            additionalImages = additionalImages,
            isPopular = isPopular,
            isNew = isNew
        )
    }
    
    private fun com.podolyanchik.hockeyshop.data.local.entity.CategoryEntity.toCategory(): Category {
        return Category(
            id = id,
            name = name,
            iconUrl = iconUrl,
            description = description
        )
    }
} 