package com.podolyanchik.hockeyshop.presentation.screen.home

import android.util.Log
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
import androidx.compose.material.icons.filled.Search
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
import com.podolyanchik.hockeyshop.presentation.component.SearchBar
import com.podolyanchik.hockeyshop.presentation.component.SortOptionsMenu
import com.podolyanchik.hockeyshop.presentation.viewmodel.AuthViewModel
import com.podolyanchik.hockeyshop.presentation.viewmodel.HomeViewModel
import com.podolyanchik.hockeyshop.presentation.viewmodel.SharedCartViewModel
import com.podolyanchik.hockeyshop.presentation.viewmodel.SortType
import com.podolyanchik.hockeyshop.util.Resource
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.podolyanchik.hockeyshop.presentation.component.EnhancedCategoryItem
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.border
import com.podolyanchik.hockeyshop.domain.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProductDetail: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAdminPanel: () -> Unit = {},
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    sharedCartViewModel: SharedCartViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val productsState by homeViewModel.productsState.collectAsStateWithLifecycle()
    val popularProductsState by homeViewModel.popularProductsState.collectAsStateWithLifecycle()
    val categoriesState by homeViewModel.categoriesState.collectAsStateWithLifecycle()
    val cartItemCount by sharedCartViewModel.cartItemCount.collectAsStateWithLifecycle()
    
    val searchQuery by homeViewModel.searchQuery.collectAsStateWithLifecycle()
    val currentSortType by homeViewModel.currentSortType.collectAsStateWithLifecycle()
    
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(currentUser) {
        Log.d("HockeyShop", "Current user: ${currentUser?.name}, role: ${currentUser?.role?.name}")
    }
    
    LaunchedEffect(key1 = Unit) {
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
                    // Only keep the logout button in the top toolbar
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
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                if (currentUser?.role == UserRole.ADMIN) {
                    // For admin users, only show Home and Profile buttons (no cart)
                    IconButton(
                        onClick = { /* Уже на главном экране */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = stringResource(R.string.home),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    
                    // Profile button for admin - this navigates to admin panel
                    IconButton(
                        onClick = onNavigateToAdminPanel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = stringResource(R.string.profile),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    IconButton(
                        onClick = { /* Уже на главном экране */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = stringResource(R.string.home),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    IconButton(
                        onClick = onNavigateToCart,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = stringResource(R.string.cart),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = stringResource(R.string.profile),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    currentUser?.let {
                        Text(
                            text = "Привет, ${it.name}!",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Text(
                            text = "Вы вошли как ${if (it.role == UserRole.ADMIN) "Администратор" else "Пользователь"}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                
                item {
                    Text(
                        text = stringResource(R.string.all_categories),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    when (categoriesState) {
                        is Resource.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        is Resource.Success -> {
                            val categories = (categoriesState as Resource.Success<List<Category>>).data
                            if (categories?.isNotEmpty() == true) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        .padding(12.dp)
                                ) {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        item {
                                            EnhancedCategoryItem(
                                                categoryName = stringResource(R.string.all_category_item),
                                                onClick = { homeViewModel.selectCategory(null) }
                                            )
                                        }
                                        items(categories) { category ->
                                            EnhancedCategoryItem(
                                                categoryName = category.name,
                                                onClick = { homeViewModel.selectCategory(category.id) }
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = stringResource(R.string.no_categories_found),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    color = MaterialTheme.colorScheme.onBackground
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
                
                item {
                    Text(
                        text = stringResource(R.string.popular),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    when (popularProductsState) {
                        is Resource.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
                                    text = stringResource(R.string.no_popular_found),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    color = MaterialTheme.colorScheme.onBackground
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
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                item {
                    Text(
                        text = stringResource(R.string.all_products),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    // Enhanced search section with background and label
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Search label with icon
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.find_products),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            // Search and sort row with improved alignment
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Search bar with weight to take most of the space
                                SearchBar(
                                    query = searchQuery,
                                    onQueryChange = { homeViewModel.searchProducts(it) },
                                    hint = stringResource(R.string.search_products),
                                    modifier = Modifier.weight(1f)
                                )
                                
                                // Sort button
                                SortOptionsMenu(
                                    currentSortType = currentSortType,
                                    onSortTypeSelected = { homeViewModel.setSortType(it) }
                                )
                            }
                        }
                    }
                    
                    // If search query is not empty, show search results title
                    if (searchQuery.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.search_results),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                
                when (productsState) {
                    is Resource.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    is Resource.Success -> {
                        val products = (productsState as Resource.Success<List<Product>>).data
                        if (products?.isNotEmpty() == true) {
                            items(products.size) { index ->
                                val product = products[index]
                                ProductItem(
                                    product = product,
                                    onClick = { onNavigateToProductDetail(product.id) },
                                    onAddToCart = { sharedCartViewModel.addToCart(product.id, 1) },
                                    isAdmin = currentUser?.role == UserRole.ADMIN
                                )
                                
                                if (index < products.size - 1) {
                                    Divider(
                                        modifier = Modifier.padding(vertical = 16.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }
                            }
                        } else {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (searchQuery.isEmpty()) 
                                                stringResource(R.string.no_products_found)
                                               else 
                                                stringResource(R.string.no_search_results),
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
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
    
    errorMessage?.let { message ->
        ErrorDialog(
            message = message,
            onDismiss = { errorMessage = null }
        )
    }
}

@Composable
fun ProductItem(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    isAdmin: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = 0.5.dp,
                color = if (isSystemInDarkTheme()) 
                    Color(0xFF3D3D3D) 
                else 
                    Color(0xFFDCE0E4),
                shape = RoundedCornerShape(12.dp)
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) 
                Color(0xFF2A2A2A) // Darker gray for dark theme
            else 
                Color(0xFFF5F7FA) // Light gray with slight blue tint
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (product.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (isSystemInDarkTheme()) 
                                        Color(0xFF353535)
                                    else 
                                        MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = product.name.first().toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isSystemInDarkTheme())
                            Color.White
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = product.category.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSystemInDarkTheme())
                            Color.White.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    val displayPrice = if (product.discount > 0) {
                        val finalPrice = product.price * (1 - product.discount / 100)
                        "$${String.format("%.2f", finalPrice)} (${product.discount.toInt()}% ${stringResource(R.string.discount)})"
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Add to Cart button - only for regular users
            if (!isAdmin) {
                Button(
                    onClick = onAddToCart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    enabled = product.quantity > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.add_to_cart),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
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
            .width(160.dp)
            .height(220.dp)
            .clickable(onClick = onClick)
            .border(
                width = 0.5.dp,
                color = if (isSystemInDarkTheme()) 
                    Color(0xFF3D3D3D) 
                else 
                    Color(0xFFDCE0E4),
                shape = RoundedCornerShape(12.dp)
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) 
                Color(0xFF2A2A2A) // Darker gray for dark theme
            else 
                Color(0xFFF5F7FA) // Light gray with slight blue tint
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (product.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (isSystemInDarkTheme()) 
                                        Color(0xFF353535)
                                    else 
                                        MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = product.name.first().toString().uppercase(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    color = if (isSystemInDarkTheme())
                        Color.White
                    else
                        MaterialTheme.colorScheme.onSurface
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
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
} 