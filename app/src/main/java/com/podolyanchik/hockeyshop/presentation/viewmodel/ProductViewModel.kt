package com.podolyanchik.hockeyshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podolyanchik.hockeyshop.domain.model.Product
import com.podolyanchik.hockeyshop.domain.repository.ProductRepository
import com.podolyanchik.hockeyshop.domain.repository.CartRepository
import com.podolyanchik.hockeyshop.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _productState = MutableStateFlow<Resource<Product>>(Resource.Initial())
    val productState: StateFlow<Resource<Product>> = _productState.asStateFlow()

    private val _isInCart = MutableStateFlow(false)
    val isInCart: StateFlow<Boolean> = _isInCart.asStateFlow()

    /**
     * Fetches product details by ID
     */
    fun fetchProductDetails(productId: String) {
        viewModelScope.launch {
            _productState.value = Resource.Loading()
            
            try {
                val result = productRepository.getProductById(productId)
                _productState.value = result
                
                // Check if the product is already in the cart
                cartRepository.isProductInCart(productId).collectLatest { isInCart ->
                    _isInCart.value = isInCart
                }
            } catch (e: Exception) {
                _productState.value = Resource.Error(e.message ?: "Error fetching product details")
            }
        }
    }

    /**
     * Adds the product to cart and decreases its stock quantity
     */
    fun addToCartAndUpdateStock(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            // Only proceed if product is in stock
            if (product.quantity <= 0) {
                _productState.value = Resource.Error("Товар отсутствует на складе")
                return@launch
            }
            
            // Check if there's enough quantity
            if (product.quantity < quantity) {
                _productState.value = Resource.Error("Недостаточное количество товара на складе")
                return@launch
            }
            
            // First add to cart
            val addToCartResult = cartRepository.addToCart(product, quantity)
            if (addToCartResult is Resource.Error) {
                _productState.value = Resource.Error(addToCartResult.message ?: "Ошибка при добавлении в корзину")
                return@launch
            }
            
            // Then decrease product stock
            val decreaseStockResult = productRepository.decreaseProductQuantity(product.id, quantity)
            if (decreaseStockResult is Resource.Error) {
                _productState.value = Resource.Error(decreaseStockResult.message ?: "Ошибка при обновлении остатков")
                return@launch
            }
            
            // Update local state to reflect the new stock level
            if (_productState.value is Resource.Success) {
                val currentProduct = (_productState.value as Resource.Success<Product>).data
                currentProduct?.let {
                    // Create new product with updated quantity
                    val updatedProduct = it.copy(quantity = it.quantity - quantity)
                    _productState.value = Resource.Success(updatedProduct)
                }
            }
            
            // Update isInCart state
            _isInCart.value = true
        }
    }

    /**
     * Clears error state
     */
    fun clearError() {
        if (_productState.value is Resource.Error) {
            _productState.value = Resource.Initial()
        }
    }
} 