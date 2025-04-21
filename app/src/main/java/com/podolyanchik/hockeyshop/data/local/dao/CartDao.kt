package com.podolyanchik.hockeyshop.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.podolyanchik.hockeyshop.data.local.entity.CartEntity
import com.podolyanchik.hockeyshop.data.local.relation.CartWithProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    /**
     * Get all cart items for a specific user with their associated products
     */
    @Transaction
    @Query("SELECT * FROM carts WHERE userId = :userId ORDER BY addedAt DESC")
    fun getCartItemsWithProductsForUser(userId: String): Flow<List<CartWithProduct>>
    
    /**
     * Get cart item count for a specific user
     */
    @Query("SELECT COUNT(*) FROM carts WHERE userId = :userId")
    fun getCartItemCount(userId: String): Flow<Int>
    
    /**
     * Get total quantity of items in cart for a specific user
     */
    @Query("SELECT SUM(quantity) FROM carts WHERE userId = :userId")
    fun getTotalQuantity(userId: String): Flow<Int?>
    
    /**
     * Check if a specific product is in a user's cart
     */
    @Query("SELECT EXISTS(SELECT 1 FROM carts WHERE userId = :userId AND productId = :productId)")
    fun isProductInCart(userId: String, productId: String): Flow<Boolean>
    
    /**
     * Add an item to the cart (or update quantity if already exists)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCartItem(cartItem: CartEntity)
    
    /**
     * Update a cart item's quantity
     */
    @Update
    suspend fun updateCartItem(cartItem: CartEntity)
    
    /**
     * Remove a cart item
     */
    @Delete
    suspend fun removeCartItem(cartItem: CartEntity)
    
    /**
     * Remove an item from cart by user ID and product ID
     */
    @Query("DELETE FROM carts WHERE userId = :userId AND productId = :productId")
    suspend fun removeCartItemByIds(userId: String, productId: String)
    
    /**
     * Clear the entire cart for a specific user
     */
    @Query("DELETE FROM carts WHERE userId = :userId")
    suspend fun clearCartForUser(userId: String)
} 