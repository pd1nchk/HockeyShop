package com.podolyanchik.hockeyshop.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podolyanchik.hockeyshop.R
import com.podolyanchik.hockeyshop.domain.model.Category
import com.podolyanchik.hockeyshop.domain.model.Product
import com.podolyanchik.hockeyshop.domain.repository.CategoryRepository
import com.podolyanchik.hockeyshop.domain.repository.ProductRepository
import com.podolyanchik.hockeyshop.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

// Перечисление для типов сортировки
enum class SortType {
    NONE,         // Без сортировки
    NAME_ASC,     // По имени (А-Я)
    NAME_DESC,    // По имени (Я-А)
    PRICE_ASC,    // По цене (возрастание)
    PRICE_DESC,   // По цене (убывание)
    DISCOUNT_ASC, // По скидке (возрастание)
    DISCOUNT_DESC // По скидке (убывание)
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _productsState = MutableStateFlow<Resource<List<Product>>>(Resource.Initial())
    val productsState: StateFlow<Resource<List<Product>>> = _productsState.asStateFlow()

    private val _popularProductsState = MutableStateFlow<Resource<List<Product>>>(Resource.Initial())
    val popularProductsState: StateFlow<Resource<List<Product>>> = _popularProductsState.asStateFlow()

    private val _categoriesState = MutableStateFlow<Resource<List<Category>>>(Resource.Initial())
    val categoriesState: StateFlow<Resource<List<Category>>> = _categoriesState.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    
    // Добавляем состояние для поиска
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Добавляем состояние для сортировки
    private val _currentSortType = MutableStateFlow(SortType.NONE)
    val currentSortType: StateFlow<SortType> = _currentSortType.asStateFlow()
    
    // Add states for product operations
    private val _productOperationState = MutableStateFlow<Resource<Unit>>(Resource.Initial())
    val productOperationState: StateFlow<Resource<Unit>> = _productOperationState.asStateFlow()

    init {
        loadProducts()
        loadPopularProducts()
        loadCategories()
        ensureCategoriesExist()
        updateCategoryNames()
    }

    private fun loadProducts() {
        _productsState.value = Resource.Loading()
        
        // Если есть поисковый запрос, ищем по нему
        if (_searchQuery.value.isNotEmpty()) {
            searchProducts(_searchQuery.value)
            return
        }
        
        // Если выбрана сортировка по цене, используем соответствующий метод репозитория
        when (_currentSortType.value) {
            SortType.PRICE_ASC -> {
                loadSortedProductsByPrice(true)
                return
            }
            SortType.PRICE_DESC -> {
                loadSortedProductsByPrice(false)
                return
            }
            else -> {} // Продолжаем обычную загрузку для других типов сортировки
        }
        
        _selectedCategoryId.value?.let { categoryId ->
            // If category is selected, load products for that category
            productRepository.getProductsByCategory(categoryId)
                .onEach { products ->
                    // Применяем сортировку по имени или скидке, если она выбрана
                    val sortedProducts = when (_currentSortType.value) {
                        SortType.NAME_ASC -> products.sortedBy { it.name }
                        SortType.NAME_DESC -> products.sortedByDescending { it.name }
                        SortType.DISCOUNT_ASC -> products.sortedBy { it.discount }
                        SortType.DISCOUNT_DESC -> products.sortedByDescending { it.discount }
                        else -> products
                    }
                    _productsState.value = Resource.Success(sortedProducts)
                }
                .catch { e ->
                    _productsState.value = Resource.Error(e.message ?: "Не удалось загрузить товары")
                }
                .launchIn(viewModelScope)
        } ?: run {
            // Otherwise, load all products
            productRepository.getAllProducts()
                .onEach { products ->
                    // Применяем сортировку по имени или скидке, если она выбрана
                    val sortedProducts = when (_currentSortType.value) {
                        SortType.NAME_ASC -> products.sortedBy { it.name }
                        SortType.NAME_DESC -> products.sortedByDescending { it.name }
                        SortType.DISCOUNT_ASC -> products.sortedBy { it.discount }
                        SortType.DISCOUNT_DESC -> products.sortedByDescending { it.discount }
                        else -> products
                    }
                    _productsState.value = Resource.Success(sortedProducts)
                }
                .catch { e ->
                    _productsState.value = Resource.Error(e.message ?: "Не удалось загрузить товары")
                }
                .launchIn(viewModelScope)
        }
    }
    
    // Метод для сортировки по цене
    private fun loadSortedProductsByPrice(ascending: Boolean) {
        productRepository.getSortedProductsByPrice(ascending)
            .onEach { products ->
                _productsState.value = Resource.Success(products)
            }
            .catch { e ->
                _productsState.value = Resource.Error(e.message ?: "Не удалось загрузить товары")
            }
            .launchIn(viewModelScope)
    }
    
    // Метод для поиска продуктов
    fun searchProducts(query: String) {
        _searchQuery.value = query
        
        if (query.isEmpty()) {
            // Если запрос пустой, возвращаемся к обычной загрузке
            _currentSortType.value = SortType.NONE
            loadProducts()
            return
        }
        
        _productsState.value = Resource.Loading()
        
        productRepository.searchProducts(query)
            .onEach { products ->
                _productsState.value = Resource.Success(products)
            }
            .catch { e ->
                _productsState.value = Resource.Error(e.message ?: "Не удалось выполнить поиск товаров")
            }
            .launchIn(viewModelScope)
    }
    
    // Метод для установки типа сортировки
    fun setSortType(sortType: SortType) {
        _currentSortType.value = sortType
        loadProducts()
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
            // Используем локализованные строки для названий категорий
            val categoryData = listOf(
                Pair(R.string.sticks, "sticks"), 
                Pair(R.string.skates, "skates"), 
                Pair(R.string.form, "uniform"), 
                Pair(R.string.equipment, "equipment")
            )
            
            categoryRepository.getAllCategories().collect { existingCategories ->
                if (existingCategories.isEmpty()) {
                    // If no categories exist, create the default ones
                    categoryData.forEachIndexed { index, (stringRes, descriptionKey) ->
                        val name = context.getString(stringRes)
                        val description = "Category for $descriptionKey"
                        
                        val category = Category(
                            id = index + 1,  // Start from 1
                            name = name,
                            description = description
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

    private fun updateCategoryNames() {
        viewModelScope.launch {
            // Список соответствий ID категорий и строковых ресурсов
            val categoryResources = mapOf(
                1 to R.string.sticks,
                2 to R.string.skates,
                3 to R.string.form,
                4 to R.string.equipment
            )
            
            categoryRepository.getAllCategories().collect { existingCategories ->
                if (existingCategories.isNotEmpty()) {
                    // Обновляем имена категорий на локализованные
                    existingCategories.forEach { category ->
                        categoryResources[category.id]?.let { stringRes ->
                            val localizedName = context.getString(stringRes)
                            
                            // Обновляем только если имя не совпадает с локализованным
                            if (category.name != localizedName) {
                                val updatedCategory = category.copy(name = localizedName)
                                categoryRepository.updateCategory(updatedCategory)
                            }
                        }
                    }
                }
            }
        }
    }
} 