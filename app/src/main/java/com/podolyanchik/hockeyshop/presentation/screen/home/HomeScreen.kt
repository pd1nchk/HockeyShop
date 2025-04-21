package com.podolyanchik.hockeyshop.presentation.screen.home

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.podolyanchik.hockeyshop.R
import com.podolyanchik.hockeyshop.domain.model.Category
import com.podolyanchik.hockeyshop.domain.model.Product
import com.podolyanchik.hockeyshop.presentation.component.ErrorDialog
import com.podolyanchik.hockeyshop.presentation.viewmodel.AuthViewModel
import com.podolyanchik.hockeyshop.presentation.viewmodel.HomeViewModel
import com.podolyanchik.hockeyshop.presentation.viewmodel.SharedCartViewModel
import com.podolyanchik.hockeyshop.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProductDetail: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToAdminPanel: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    sharedCartViewModel: SharedCartViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val productsState by homeViewModel.productsState.collectAsStateWithLifecycle()
    val popularProductsState by homeViewModel.popularProductsState.collectAsStateWithLifecycle()
    val categoriesState by homeViewModel.categoriesState.collectAsStateWithLifecycle()
    val cartItemCount by sharedCartViewModel.cartItemCount.collectAsStateWithLifecycle()
    
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Add LaunchedEffect to refresh cart data whenever HomeScreen is displayed
    LaunchedEffect(key1 = Unit) {
        // Refresh cart data to ensure badge is updated when returning from other screens
        sharedCartViewModel.loadCart()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    // Admin panel button (only for admins)
                    if (currentUser?.role?.name == "ADMIN") {
                        IconButton(onClick = onNavigateToAdminPanel) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = stringResource(R.string.admin_panel)
                            )
                        }
                    }

                    // Logout button
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = stringResource(R.string.logout)
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                IconButton(
                    onClick = { /* Уже на главном экране */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = stringResource(R.string.home)
                    )
                }

                if (currentUser?.role?.name != "ADMIN") {
                    IconButton(
                        onClick = onNavigateToCart,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = stringResource(R.string.cart)
                            )
                            
                            // Show badge only if there are items in cart
                            if (cartItemCount > 0) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 10.dp, y = (-10).dp)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.error)
                                ) {
                                    Text(
                                        text = if (cartItemCount > 99) "99+" else cartItemCount.toString(),
                                        color = MaterialTheme.colorScheme.onError,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = stringResource(R.string.profile)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    // Приветствие пользователя
                    currentUser?.let {
                        Text(
                            text = "Привет, ${it.name}!",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                        
                        Text(
                            text = "Вы вошли как ${if (it.role.name == "ADMIN") "Администратор" else "Пользователь"}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
                
                // Categories section
                item {
                    Text(
                        text = stringResource(R.string.all_categories),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    when (categoriesState) {
                        is Resource.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        is Resource.Success -> {
                            val categories = (categoriesState as Resource.Success<List<Category>>).data
                            if (categories?.isNotEmpty() == true) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    item {
                                        CategoryItem(
                                            categoryName = "All",
                                            onClick = { homeViewModel.selectCategory(null) }
                                        )
                                    }
                                    items(categories) { category ->
                                        CategoryItem(
                                            categoryName = category.name,
                                            onClick = { homeViewModel.selectCategory(category.id) }
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = "No categories found",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }
                        }
                        is Resource.Error -> {
                            val error = (categoriesState as Resource.Error<List<Category>>).message
                            errorMessage = error
                            Text(
                                text = "Error loading categories: $error",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                        is Resource.Initial -> {
                            // Initial state, nothing to show yet
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Popular products section
                item {
                    Text(
                        text = stringResource(R.string.popular),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    when (popularProductsState) {
                        is Resource.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        is Resource.Success -> {
                            val popularProducts = (popularProductsState as Resource.Success<List<Product>>).data
                            if (popularProducts?.isNotEmpty() == true) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    items(popularProducts) { product ->
                                        CompactProductItem(
                                            product = product,
                                            onClick = { onNavigateToProductDetail(product.id) }
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = "No popular products found",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }
                        }
                        is Resource.Error -> {
                            val error = (popularProductsState as Resource.Error<List<Product>>).message
                            errorMessage = error
                            Text(
                                text = "Error loading popular products: $error",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                        is Resource.Initial -> {
                            // Initial state, nothing to show yet
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // All products section
                item {
                    Text(
                        text = stringResource(R.string.all_products),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Products list
                when (productsState) {
                    is Resource.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    is Resource.Success -> {
                        val products = (productsState as Resource.Success<List<Product>>).data
                        if (products?.isNotEmpty() == true) {
                            items(products) { product ->
                                ProductItem(
                                    product = product,
                                    onClick = { onNavigateToProductDetail(product.id) }
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        } else {
                            item {
                                Text(
                                    text = "No products found",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        item {
                            val error = (productsState as Resource.Error<List<Product>>).message
                            errorMessage = error
                            Text(
                                text = "Error loading products: $error",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
                    is Resource.Initial -> {
                        // Initial state, nothing to show yet
                    }
                }
            }
        }
    }
    
    // Show error dialog if there's an error
    errorMessage?.let { message ->
        ErrorDialog(
            message = message,
            onDismiss = { errorMessage = null }
        )
    }
}

@Composable
fun CategoryItem(
    categoryName: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            // Category icon would go here - using first letter as placeholder
            Text(
                text = categoryName.first().toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = categoryName,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProductItem(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image placeholder or actual image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                // In a real app, you would load the image from product.imageUrl
                Text(
                    text = product.name.first().toString(),
                    style = MaterialTheme.typography.headlineMedium,
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
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = product.category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                val displayPrice = if (product.discount > 0) {
                    val finalPrice = product.price * (1 - product.discount / 100)
                    "$${String.format("%.2f", finalPrice)} (${product.discount.toInt()}% off)"
                } else {
                    "$${String.format("%.2f", product.price)}"
                }
                
                Text(
                    text = displayPrice,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CompactProductItem(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Product image placeholder or actual image
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                // In a real app, you would load the image from product.imageUrl
                Text(
                    text = product.name.first().toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            val displayPrice = if (product.discount > 0) {
                val finalPrice = product.price * (1 - product.discount / 100)
                "$${String.format("%.2f", finalPrice)}"
            } else {
                "$${String.format("%.2f", product.price)}"
            }
            
            Text(
                text = displayPrice,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
} 