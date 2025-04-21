package com.podolyanchik.hockeyshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podolyanchik.hockeyshop.domain.model.Product
import com.podolyanchik.hockeyshop.domain.repository.ProductRepository
import com.podolyanchik.hockeyshop.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.UUID

/**
 * ViewModel for managing the shopping cart
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    // Cart items with quantities
    private val _cartItems = MutableStateFlow<Map<Product, Int>>(emptyMap())
    val cartItems: StateFlow<Map<Product, Int>> = _cartItems.asStateFlow()
    
    // Total price of items in cart
    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadCart()
    }
    

    fun loadCart() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // In a real app, you would get this from a repository
                // For now, we're creating some dummy data
                val mockCartItems = mutableMapOf<Product, Int>()
                
                // Get some products from the repository to simulate cart items
                val products = productRepository.getAllProducts().collect { products ->
                    if (products.isNotEmpty()) {
                        // Add first 2 products to cart for demo
                        val cartProducts = products.take(2)
                        cartProducts.forEach { product ->
                            mockCartItems[product] = 1
                        }
                        _cartItems.value = mockCartItems
                        updateTotalPrice()
                    }
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading cart"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Add item to cart
     */
    fun addToCart(product: Product) {
        val currentItems = _cartItems.value.toMutableMap()
        val currentQty = currentItems[product] ?: 0
        currentItems[product] = currentQty + 1
        
        _cartItems.value = currentItems
        updateTotalPrice()
    }
    
    /**
     * Remove item from cart
     */
    fun removeFromCart(product: Product) {
        val currentItems = _cartItems.value.toMutableMap()
        currentItems.remove(product)
        
        _cartItems.value = currentItems
        updateTotalPrice()
    }
    
    /**
     * Update item quantity
     */
    fun updateQuantity(product: Product, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(product)
            return
        }
        
        val currentItems = _cartItems.value.toMutableMap()
        currentItems[product] = quantity
        
        _cartItems.value = currentItems
        updateTotalPrice()
    }
    
    /**
     * Clear the entire cart
     */
    fun clearCart() {
        _cartItems.value = emptyMap()
        _totalPrice.value = 0.0
    }
    
    /**
     * Update total price based on items and quantities
     */
    private fun updateTotalPrice() {
        var total = 0.0
        
        _cartItems.value.forEach { (product, quantity) ->
            // If product has discount, use final price, otherwise use regular price
            val itemPrice = if (product.discount > 0) {
                product.finalPrice
            } else {
                product.price
            }
            
            total += itemPrice * quantity
        }
        
        _totalPrice.value = total
    }
    
    /**
     * Clear error state
     */
    fun clearError() {
        _error.value = null
    }
} 