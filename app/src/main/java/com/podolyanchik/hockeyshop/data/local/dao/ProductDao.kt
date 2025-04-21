package com.podolyanchik.hockeyshop.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.podolyanchik.hockeyshop.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)
    
    @Update
    suspend fun updateProduct(product: ProductEntity)
    
    @Delete
    suspend fun deleteProduct(product: ProductEntity)
    
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): ProductEntity?
    
    @Query("SELECT * FROM products WHERE id = :productId")
    fun observeProductById(productId: String): Flow<ProductEntity?>
    
    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    fun getProductsByCategory(categoryId: Int): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products ORDER BY price ASC")
    fun getProductsOrderedByPriceAsc(): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products ORDER BY price DESC")
    fun getProductsOrderedByPriceDesc(): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products ORDER BY rating DESC")
    fun getProductsOrderedByRating(): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products WHERE isPopular = 1")
    fun getPopularProducts(): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products WHERE isNew = 1")
    fun getNewProducts(): Flow<List<ProductEntity>>
    
    @Query("UPDATE products SET quantity = quantity - :amount WHERE id = :productId")
    suspend fun decreaseProductQuantity(productId: String, amount: Int)
    
    @Query("UPDATE products SET quantity = quantity + :amount WHERE id = :productId")
    suspend fun increaseProductQuantity(productId: String, amount: Int)
} 