package com.podolyanchik.hockeyshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podolyanchik.hockeyshop.domain.model.Category
import com.podolyanchik.hockeyshop.domain.model.Product
import com.podolyanchik.hockeyshop.domain.repository.CategoryRepository
import com.podolyanchik.hockeyshop.domain.repository.ProductRepository
import com.podolyanchik.hockeyshop.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _productsState = MutableStateFlow<Resource<List<Product>>>(Resource.Initial())
    val productsState: StateFlow<Resource<List<Product>>> = _productsState.asStateFlow()

    private val _popularProductsState = MutableStateFlow<Resource<List<Product>>>(Resource.Initial())
    val popularProductsState: StateFlow<Resource<List<Product>>> = _popularProductsState.asStateFlow()

    private val _categoriesState = MutableStateFlow<Resource<List<Category>>>(Resource.Initial())
    val categoriesState: StateFlow<Resource<List<Category>>> = _categoriesState.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    
    // Add states for product operations
    private val _productOperationState = MutableStateFlow<Resource<Unit>>(Resource.Initial())
    val productOperationState: StateFlow<Resource<Unit>> = _productOperationState.asStateFlow()

    init {
        loadProducts()
        loadPopularProducts()
        loadCategories()
        ensureCategoriesExist()
    }

    private fun loadProducts() {
        _productsState.value = Resource.Loading()
        
        _selectedCategoryId.value?.let { categoryId ->
            // If category is selected, load products for that category
            productRepository.getProductsByCategory(categoryId)
                .onEach { products ->
                    _productsState.value = Resource.Success(products)
                }
                .catch { e ->
                    _productsState.value = Resource.Error(e.message ?: "Не удалось загрузить товары")
                }
                .launchIn(viewModelScope)
        } ?: run {
            // Otherwise, load all products
            productRepository.getAllProducts()
                .onEach { products ->
                    _productsState.value = Resource.Success(products)
                }
                .catch { e ->
                    _productsState.value = Resource.Error(e.message ?: "Не удалось загрузить товары")
                }
                .launchIn(viewModelScope)
        }
    }
    
    // Function to load all products for the admin panel
    fun loadAllProducts() {
        loadProducts()
    }

    private fun loadPopularProducts() {
        _popularProductsState.value = Resource.Loading()
        
        productRepository.getPopularProducts()
            .onEach { products ->
                _popularProductsState.value = Resource.Success(products)
            }
            .catch { e ->
                _popularProductsState.value = Resource.Error(e.message ?: "Не удалось загрузить популярные товары")
            }
            .launchIn(viewModelScope)
    }

    fun loadCategories() {
        _categoriesState.value = Resource.Loading()
        
        categoryRepository.getAllCategories()
            .onEach { categories ->
                _categoriesState.value = Resource.Success(categories)
            }
            .catch { e ->
                _categoriesState.value = Resource.Error(e.message ?: "Не удалось загрузить категории")
            }
            .launchIn(viewModelScope)
    }

    fun selectCategory(categoryId: Int?) {
        _selectedCategoryId.value = categoryId
        loadProducts()
    }

    private fun ensureCategoriesExist() {
        viewModelScope.launch {
            val categories = listOf("Sticks", "Skates", "Uniform", "Equipment")
            
            categoryRepository.getAllCategories().collect { existingCategories ->
                if (existingCategories.isEmpty()) {
                    // If no categories exist, create the default ones
                    categories.forEachIndexed { index, name ->
                        val category = Category(
                            id = index + 1,  // Start from 1
                            name = name,
                            description = "Category for $name"
                        )
                        categoryRepository.addCategory(category)
                    }
                }
            }
        }
    }
    
    /**
     * Creates a new product
     */
    fun createProduct(
        name: String,
        description: String,
        price: Double,
        imageUrl: String,
        categoryId: Int,
        quantity: Int,
        discount: Float,
        isPopular: Boolean,
        isNew: Boolean
    ) {
        viewModelScope.launch {
            _productOperationState.value = Resource.Loading()
            
            // Get the category
            val categoryResource = categoryRepository.getCategoryById(categoryId)
            when (categoryResource) {
                is Resource.Success -> {
                    val category = categoryResource.data
                    if (category != null) {
                        // Create new product with unique ID
                        val newProductId = UUID.randomUUID().toString()
                        val newProduct = Product(
                            id = newProductId,
                            name = name,
                            description = description,
                            price = price,
                            imageUrl = imageUrl,
                            category = category,
                            quantity = quantity,
                            discount = discount,
                            isPopular = isPopular,
                            isNew = isNew
                        )
                        
                        // Add the product
                        val result = productRepository.addProduct(newProduct)
                        _productOperationState.value = result
                        
                        // Reload products if successful
                        if (result is Resource.Success) {
                            loadAllProducts()
                            if (isPopular) {
                                loadPopularProducts()
                            }
                        }
                    } else {
                        _productOperationState.value = Resource.Error("Category not found")
                    }
                }
                is Resource.Error -> {
                    _productOperationState.value = Resource.Error(categoryResource.message ?: "Failed to get category")
                }
                else -> {
                    _productOperationState.value = Resource.Error("Unexpected error occurred")
                }
            }
        }
    }
    
    /**
     * Updates an existing product
     */
    fun updateProduct(
        id: String,
        name: String,
        description: String,
        price: Double,
        imageUrl: String,
        categoryId: Int,
        quantity: Int,
        discount: Float,
        isPopular: Boolean,
        isNew: Boolean
    ) {
        viewModelScope.launch {
            _productOperationState.value = Resource.Loading()
            
            // Get the category
            val categoryResource = categoryRepository.getCategoryById(categoryId)
            when (categoryResource) {
                is Resource.Success -> {
                    val category = categoryResource.data
                    if (category != null) {
                        // Create updated product
                        val updatedProduct = Product(
                            id = id,
                            name = name,
                            description = description,
                            price = price,
                            imageUrl = imageUrl,
                            category = category,
                            quantity = quantity,
                            discount = discount,
                            isPopular = isPopular,
                            isNew = isNew
                        )
                        
                        // Update the product
                        val result = productRepository.updateProduct(updatedProduct)
                        _productOperationState.value = result
                        
                        // Reload products if successful
                        if (result is Resource.Success) {
                            loadAllProducts()
                            loadPopularProducts() // Reload in case popular status changed
                        }
                    } else {
                        _productOperationState.value = Resource.Error("Category not found")
                    }
                }
                is Resource.Error -> {
                    _productOperationState.value = Resource.Error(categoryResource.message ?: "Failed to get category")
                }
                else -> {
                    _productOperationState.value = Resource.Error("Unexpected error occurred")
                }
            }
        }
    }
    
    /**
     * Deletes a product
     */
    fun deleteProduct(id: String) {
        viewModelScope.launch {
            _productOperationState.value = Resource.Loading()
            
            val result = productRepository.deleteProduct(id)
            _productOperationState.value = result
            
            // Reload products if successful
            if (result is Resource.Success) {
                loadAllProducts()
                loadPopularProducts() // Reload in case the deleted product was popular
            }
        }
    }
    
    /**
     * Reset product operation state
     */
    fun resetProductOperationState() {
        _productOperationState.value = Resource.Initial()
    }
} 