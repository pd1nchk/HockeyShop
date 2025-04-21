package com.podolyanchik.hockeyshop.domain.repository

import com.podolyanchik.hockeyshop.domain.model.Product
import com.podolyanchik.hockeyshop.util.Resource
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    
    fun getAllProducts(): Flow<List<Product>>
    
    suspend fun getProductById(id: String): Resource<Product>
    
    fun observeProductById(id: String): Flow<Product?>
    
    fun getProductsByCategory(categoryId: Int): Flow<List<Product>>
    
    fun searchProducts(query: String): Flow<List<Product>>
    
    fun getSortedProductsByPrice(ascending: Boolean): Flow<List<Product>>
    
    fun getProductsByPopularity(): Flow<List<Product>>
    
    fun getNewProducts(): Flow<List<Product>>

    fun getPopularProducts(): Flow<List<Product>>
    
    suspend fun addProduct(product: Product): Resource<Unit>
    
    suspend fun updateProduct(product: Product): Resource<Unit>
    
    suspend fun deleteProduct(id: String): Resource<Unit>
    
    suspend fun decreaseProductQuantity(id: String, amount: Int): Resource<Unit>
    
    suspend fun increaseProductQuantity(id: String, amount: Int): Resource<Unit>
} 