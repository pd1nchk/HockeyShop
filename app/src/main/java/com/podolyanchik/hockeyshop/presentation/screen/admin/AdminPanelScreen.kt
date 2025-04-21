package com.podolyanchik.hockeyshop.presentation.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.podolyanchik.hockeyshop.R
import com.podolyanchik.hockeyshop.domain.model.Category
import com.podolyanchik.hockeyshop.domain.model.Product
import com.podolyanchik.hockeyshop.presentation.component.ErrorDialog
import com.podolyanchik.hockeyshop.presentation.component.ConfirmationDialog
import com.podolyanchik.hockeyshop.presentation.viewmodel.HomeViewModel
import com.podolyanchik.hockeyshop.util.Resource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val productsState by viewModel.productsState.collectAsStateWithLifecycle()
    val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()
    val productOperationState by viewModel.productOperationState.collectAsStateWithLifecycle()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showAddEditProductDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }
    
    // Load products if not already loaded
    LaunchedEffect(Unit) {
        if (productsState !is Resource.Success) {
            viewModel.loadAllProducts()
        }
        if (categoriesState !is Resource.Success) {
            viewModel.loadCategories()
        }
    }
    
    // Handle product operation state changes
    LaunchedEffect(productOperationState) {
        when (productOperationState) {
            is Resource.Success -> {
                val message = when {
                    editingProduct != null -> "Product updated successfully"
                    productToDelete != null -> "Product deleted successfully"
                    else -> "Product created successfully"
                }
                scope.launch {
                    snackbarHostState.showSnackbar(message)
                }
                // Reset states
                editingProduct = null
                productToDelete = null
                showAddEditProductDialog = false
                showDeleteConfirmationDialog = false
                viewModel.resetProductOperationState()
            }
            is Resource.Error -> {
                val error = (productOperationState as Resource.Error<Unit>).message ?: "Unknown error"
                errorMessage = error
                showErrorDialog = true
                viewModel.resetProductOperationState()
            }
            else -> { /* Loading or Initial state, do nothing */ }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.admin_panel)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingProduct = null
                    showAddEditProductDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Product",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Products list section
            Text(
                text = stringResource(R.string.manage_products),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            
            when (productsState) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Success -> {
                    val products = (productsState as Resource.Success<List<Product>>).data
                    if (products?.isNotEmpty() == true) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            items(products) { product ->
                                AdminProductItem(
                                    product = product,
                                    onEdit = {
                                        editingProduct = product
                                        showAddEditProductDialog = true
                                    },
                                    onDelete = {
                                        productToDelete = product
                                        showDeleteConfirmationDialog = true
                                    },
                                    onProductClick = {
                                        onNavigateToProductDetail(product.id)
                                    }
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No products found. Add some products!",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    errorMessage = (productsState as Resource.Error<List<Product>>).message ?: "Unknown error"
                    showErrorDialog = true
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error loading products: $errorMessage",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is Resource.Initial -> {
                    // Initial state, nothing to show yet
                }
            }
        }
    }
    
    // Show error dialog if there's an error
    if (showErrorDialog) {
        ErrorDialog(
            message = errorMessage,
            onDismiss = { showErrorDialog = false }
        )
    }
    
    // Show delete confirmation dialog
    if (showDeleteConfirmationDialog && productToDelete != null) {
        ConfirmationDialog(
            title = stringResource(R.string.delete_product),
            message = stringResource(R.string.delete_confirmation),
            confirmButton = stringResource(R.string.yes),
            dismissButton = stringResource(R.string.no),
            onConfirm = {
                productToDelete?.id?.let { productId ->
                    viewModel.deleteProduct(productId)
                }
            },
            onDismiss = {
                showDeleteConfirmationDialog = false
                productToDelete = null
            }
        )
    }
    
    // Show add/edit product dialog
    if (showAddEditProductDialog) {
        // Get categories from the state
        val categories = when (categoriesState) {
            is Resource.Success -> (categoriesState as Resource.Success<List<Category>>).data ?: emptyList()
            else -> emptyList()
        }
        
        AddEditProductDialog(
            product = editingProduct,
            categories = categories,
            onDismiss = { 
                showAddEditProductDialog = false 
                editingProduct = null
            },
            onConfirm = { id, name, description, price, imageUrl, categoryId, quantity, discount, isPopular, isNew ->
                if (editingProduct == null) {
                    // Create new product
                    viewModel.createProduct(
                        name = name,
                        description = description,
                        price = price,
                        imageUrl = imageUrl,
                        categoryId = categoryId.toInt(),
                        quantity = quantity,
                        discount = discount,
                        isPopular = isPopular,
                        isNew = isNew
                    )
                } else {
                    // Update existing product
                    viewModel.updateProduct(
                        id = id,
                        name = name,
                        description = description,
                        price = price,
                        imageUrl = imageUrl,
                        categoryId = categoryId.toInt(),
                        quantity = quantity,
                        discount = discount,
                        isPopular = isPopular,
                        isNew = isNew
                    )
                }
            }
        )
    }
}

@Composable
fun AdminProductItem(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onProductClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onProductClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.name.first().toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "ID: ${product.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "Category: ${product.category.name}",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Text(
                    text = "Price: $${String.format("%.2f", product.price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            IconButton(
                onClick = onEdit
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
} 