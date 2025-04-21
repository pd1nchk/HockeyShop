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
     * Clears error state
     */
    fun clearError() {
        if (_productState.value is Resource.Error) {
            _productState.value = Resource.Initial()
        }
    }
} 