package com.podolyanchik.hockeyshop.presentation.screen.product

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.podolyanchik.hockeyshop.R
import com.podolyanchik.hockeyshop.domain.model.Product
import com.podolyanchik.hockeyshop.presentation.component.ErrorDialog
import com.podolyanchik.hockeyshop.presentation.component.HockeyShopButton
import com.podolyanchik.hockeyshop.presentation.viewmodel.ProductViewModel
import com.podolyanchik.hockeyshop.presentation.viewmodel.ProfileViewModel
import com.podolyanchik.hockeyshop.presentation.viewmodel.SharedCartViewModel
import com.podolyanchik.hockeyshop.util.Resource
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    onAddToCart: (String) -> Unit,
    viewModel: ProductViewModel = hiltViewModel(),
    sharedCartViewModel: SharedCartViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val productState by viewModel.productState.collectAsStateWithLifecycle()
    val isInCart by viewModel.isInCart.collectAsStateWithLifecycle()
    val currentUser by profileViewModel.currentUser.collectAsStateWithLifecycle()
    
    // Определяем, является ли пользователь администратором
    val isAdmin = currentUser?.role?.name == "ADMIN"
    
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    LaunchedEffect(productId) {
        viewModel.fetchProductDetails(productId)
    }
    
    // Monitor for error state
    LaunchedEffect(productState) {
        if (productState is Resource.Error) {
            errorMessage = (productState as Resource.Error).message ?: "Произошла ошибка"
            showErrorDialog = true
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.product_details)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (productState) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Success -> {
                    val product = (productState as Resource.Success<Product>).data
                    if (product != null) {
                        ProductDetailContent(
                            product = product,
                            isInCart = isInCart,
                            isAdmin = isAdmin,
                            onAddToCart = { quantity -> 
                                viewModel.addToCartAndUpdateStock(product, quantity)
                                onAddToCart(productId) 
                            }
                        )
                    } else {
                        // Product not found
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Product not found",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    // Error message already shown in dialog
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (productState as Resource.Error).message ?: "Неизвестная ошибка",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                else -> { /* Initial state, do nothing */ }
            }
            
            if (showErrorDialog) {
                ErrorDialog(
                    message = errorMessage,
                    onDismiss = { 
                        showErrorDialog = false 
                        viewModel.clearError()
                    }
                )
            }
        }
    }
}

@Composable
fun ProductDetailContent(
    product: Product,
    isInCart: Boolean,
    isAdmin: Boolean,
    onAddToCart: (Int) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Product Image
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUrl.isNotEmpty()) {
                    // Используем обычный AsyncImage с аргументами, подходящими для Coil 2.5.0
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback to placeholder image
                    Image(
                        painter = painterResource(id = R.drawable.placeholder_image),
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Product Title
        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Category
        Text(
            text = product.category.name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Price with Discount or Out of Stock message
        if (product.quantity <= 0) {
            // Out of stock message
            Text(
                text = stringResource(R.string.out_of_stock_message),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            )
        } else if (product.discount > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${String.format("%.2f", product.finalPrice)}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "  $${String.format("%.2f", product.price)}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Normal,
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    text = " (${product.discount.toInt()}%)",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.error
                    )
                )
            }
        } else {
            // Regular price
            Text(
                text = "$${String.format("%.2f", product.price)}",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Product Description
        Text(
            text = stringResource(R.string.description),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = product.description,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Visible
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Specifications
        Text(
            text = stringResource(R.string.specs),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Specs rows
        SpecificationRow(stringResource(R.string.price), "$${String.format("%.2f", product.price)}")
        
        if (product.discount > 0) {
            SpecificationRow(stringResource(R.string.discount), "${product.discount.toInt()}%")
        }
        
        // Stock status with different colors
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(if (product.quantity > 0) R.string.in_stock else R.string.out_of_stock),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (product.quantity > 0) product.quantity.toString() else "0",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (product.quantity > 0) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.error
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Quantity selector и кнопка "Добавить в корзину" - только для обычных пользователей
        if (!isAdmin) {
            if (product.quantity > 0 && !isInCart) {
                Text(
                    text = stringResource(R.string.quantity),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = stringResource(R.string.decrease_quantity),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .width(48.dp)
                            .padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center
                    )
                    
                    IconButton(
                        onClick = { if (quantity < product.quantity) quantity++ },
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.increase_quantity),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = stringResource(R.string.item_total, "$${String.format("%.2f", product.finalPrice * quantity)}"),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Add to Cart Button для обычного пользователя
            val buttonText = if (isInCart) {
                stringResource(R.string.already_in_cart)
            } else {
                stringResource(R.string.add_to_cart)
            }
            
            HockeyShopButton(
                text = buttonText,
                onClick = { if (!isInCart) onAddToCart(quantity) },
                enabled = !isInCart && product.quantity > 0,
                modifier = Modifier.fillMaxWidth()
            )
        }
        // Для админа никаких кнопок не отображаем
    }
}

@Composable
fun SpecificationRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
} 