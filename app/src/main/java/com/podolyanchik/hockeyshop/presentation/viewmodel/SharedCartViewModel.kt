package com.podolyanchik.hockeyshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podolyanchik.hockeyshop.domain.model.Product
import com.podolyanchik.hockeyshop.domain.repository.CartRepository
import com.podolyanchik.hockeyshop.domain.repository.ProductRepository
import com.podolyanchik.hockeyshop.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A shared ViewModel for cart operations that can be accessed across different screens
 * This allows us to show the cart badge count on the bottom navigation bar
 */
@HiltViewModel
class SharedCartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    // Cart items with quantities
    private val _cartItems = MutableStateFlow<Map<Product, Int>>(emptyMap())
    val cartItems: StateFlow<Map<Product, Int>> = _cartItems.asStateFlow()
    
    // Total count of items in cart
    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()
    
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
        observeCartItemCount()
        observeCartTotal()
    }
    
    fun loadCart() {
        viewModelScope.launch {
            _isLoading.value = true
            
            cartRepository.getCartItems()
                .onEach { result ->
                    _isLoading.value = false
                    
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { cartItems ->
                                _cartItems.value = cartItems
                            }
                        }
                        is Resource.Error -> {
                            _error.value = result.message
                        }
                        else -> {} // No special handling for other states
                    }
                }
                .launchIn(viewModelScope)
        }
    }
    
    private fun observeCartItemCount() {
        cartRepository.getCartItemCount()
            .onEach { count ->
                _cartItemCount.value = count
            }
            .launchIn(viewModelScope)
    }
    
    private fun observeCartTotal() {
        cartRepository.getCartTotal()
            .onEach { total ->
                _totalPrice.value = total
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Add item to cart by product id
     */
    fun addToCart(productId: String, quantity: Int = 1) {
        viewModelScope.launch {
            val result = cartRepository.addToCartById(productId, quantity)
            
            if (result is Resource.Error) {
                _error.value = result.message
            }
        }
    }
    
    /**
     * Add item to cart
     */
    fun addToCart(product: Product) {
        viewModelScope.launch {
            // Get current quantity if product already exists in cart
            val currentQuantity = _cartItems.value[product] ?: 0
            val result = cartRepository.addToCart(product, currentQuantity + 1)
            
            if (result is Resource.Error) {
                _error.value = result.message
            }
        }
    }
    
    /**
     * Remove item from cart
     */
    fun removeFromCart(product: Product) {
        viewModelScope.launch {
            val result = cartRepository.removeFromCart(product)
            
            if (result is Resource.Error) {
                _error.value = result.message
            }
        }
    }
    
    /**
     * Update item quantity
     */
    fun updateQuantity(product: Product, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(product)
            return
        }
        
        viewModelScope.launch {
            val result = cartRepository.updateQuantity(product, quantity)
            
            if (result is Resource.Error) {
                _error.value = result.message
            }
        }
    }
    
    /**
     * Decrease product stock quantity
     */
    fun decreaseProductStock(productId: String, quantity: Int) {
        viewModelScope.launch {
            val result = productRepository.decreaseProductQuantity(productId, quantity)
            
            if (result is Resource.Error) {
                _error.value = result.message
            }
        }
    }
    
    /**
     * Clear the entire cart
     */
    fun clearCart() {
        viewModelScope.launch {
            val result = cartRepository.clearCart()
            
            if (result is Resource.Error) {
                _error.value = result.message
            }
        }
    }
    
    /**
     * Clear error state
     */
    fun clearError() {
        _error.value = null
    }
} 