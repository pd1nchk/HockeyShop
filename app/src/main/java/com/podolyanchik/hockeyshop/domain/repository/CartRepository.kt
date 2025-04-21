package com.podolyanchik.hockeyshop.domain.repository

import com.podolyanchik.hockeyshop.domain.model.Product
import com.podolyanchik.hockeyshop.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for cart operations
 */
interface CartRepository {
    /**
     * Get all items in the user's cart
     */
    fun getCartItems(): Flow<Resource<Map<Product, Int>>>
    
    /**
     * Get the total number of items in the cart (sum of quantities)
     */
    fun getCartItemCount(): Flow<Int>
    
    /**
     * Get the total price of all items in the cart
     */
    fun getCartTotal(): Flow<Double>
    
    /**
     * Check if a product is already in the cart
     */
    fun isProductInCart(productId: String): Flow<Boolean>
    
    /**
     * Add a product to the cart
     */
    suspend fun addToCart(product: Product, quantity: Int = 1): Resource<Unit>
    
    /**
     * Update the quantity of a product in the cart
     */
    suspend fun updateQuantity(product: Product, quantity: Int): Resource<Unit>
    
    /**
     * Remove a product from the cart
     */
    suspend fun removeFromCart(product: Product): Resource<Unit>
    
    /**
     * Clear all items from the cart
     */
    suspend fun clearCart(): Resource<Unit>
} 