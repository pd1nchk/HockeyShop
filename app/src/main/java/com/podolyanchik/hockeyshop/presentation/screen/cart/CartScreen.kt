package com.podolyanchik.hockeyshop.presentation.screen.cart

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.podolyanchik.hockeyshop.R
import com.podolyanchik.hockeyshop.domain.model.Product
import com.podolyanchik.hockeyshop.presentation.component.CartItemRow
import com.podolyanchik.hockeyshop.presentation.component.ErrorDialog
import com.podolyanchik.hockeyshop.presentation.component.HockeyShopButton
import com.podolyanchik.hockeyshop.presentation.viewmodel.CartViewModel
import com.podolyanchik.hockeyshop.presentation.viewmodel.OrderViewModel
import com.podolyanchik.hockeyshop.presentation.viewmodel.ProfileViewModel
import com.podolyanchik.hockeyshop.presentation.viewmodel.SharedCartViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToOrders: () -> Unit,
    viewModel: SharedCartViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel()
) {
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val totalPrice by viewModel.totalPrice.collectAsStateWithLifecycle()
    val isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val currentUser by profileViewModel.currentUser.collectAsStateWithLifecycle()
    
    var showErrorDialog by remember { mutableStateOf(false) }
    var showProfileDataMissingDialog by remember { mutableStateOf(false) }
    
    // Add SnackbarHostState and coroutineScope
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    // Get string resource ahead of time for use in coroutine
    val orderPlacedSuccessMessage = stringResource(R.string.order_success_message)
    
    LaunchedEffect(error) {
        if (error != null) {
            showErrorDialog = true
        }
    }
    
    // Profile data missing dialog
    if (showProfileDataMissingDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDataMissingDialog = false },
            title = { Text(text = stringResource(R.string.profile_incomplete_title)) },
            text = { Text(text = stringResource(R.string.profile_incomplete_message)) },
            confirmButton = {
                HockeyShopButton(
                    text = stringResource(R.string.go_to_profile),
                    onClick = {
                        showProfileDataMissingDialog = false
                        onNavigateToProfile()
                    }
                )
            },
            dismissButton = {
                HockeyShopButton(
                    text = stringResource(R.string.cancel),
                    onClick = { showProfileDataMissingDialog = false },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.cart)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToOrders) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = stringResource(R.string.order_history)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (cartItems.isEmpty()) {
                // Empty cart state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(120.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = stringResource(R.string.empty_cart),
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        HockeyShopButton(
                            text = stringResource(R.string.continue_shopping),
                            onClick = onNavigateToHome
                        )
                    }
                }
            } else {
                // Cart with items
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Cart items list (takes up most of the space)
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(cartItems.entries.toList()) { (product, quantity) ->
                            CartItemRow(
                                product = product,
                                quantity = quantity,
                                onQuantityIncrease = {
                                    viewModel.updateQuantity(product, quantity + 1)
                                },
                                onQuantityDecrease = {
                                    if (quantity > 1) {
                                        viewModel.updateQuantity(product, quantity - 1)
                                    }
                                },
                                onRemoveItem = {
                                    viewModel.removeFromCart(product)
                                }
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    
                    // Order summary card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.order_summary),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(R.string.subtotal),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "$${String.format("%.2f", totalPrice)}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // Assuming a flat shipping fee for simplicity
                            val shippingFee = 5.0
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(R.string.shipping),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "$${String.format("%.2f", shippingFee)}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(R.string.total),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$${String.format("%.2f", totalPrice + shippingFee)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            HockeyShopButton(
                                text = stringResource(R.string.proceed_to_checkout),
                                onClick = {
                                    // Check if user profile has address and payment method
                                    val isAddressFilled = !currentUser?.address.isNullOrBlank()
                                    val isPaymentMethodFilled = !currentUser?.paymentMethod.isNullOrBlank()
                                    
                                    if (isAddressFilled && isPaymentMethodFilled) {
                                        // Create order
                                        currentUser?.let { user ->
                                            orderViewModel.createOrderFromCart(
                                                cartItems = cartItems,
                                                currentUser = user,
                                                shippingCost = shippingFee
                                            )
                                            
                                            // Decrease product stock
                                            for ((product, quantity) in cartItems) {
                                                viewModel.decreaseProductStock(product.id, quantity)
                                            }
                                            
                                            // Clear the cart
                                            viewModel.clearCart()
                                            
                                            // Show success message
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar(orderPlacedSuccessMessage)
                                            }
                                            
                                            // Navigate to checkout
                                            onNavigateToCheckout()
                                        }
                                    } else {
                                        // Show dialog to fill profile
                                        showProfileDataMissingDialog = true
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            HockeyShopButton(
                                text = stringResource(R.string.continue_shopping),
                                onClick = onNavigateToHome,
                                modifier = Modifier.fillMaxWidth(),
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Show error dialog if there's an error
    if (showErrorDialog && error != null) {
        ErrorDialog(
            message = error ?: "",
            onDismiss = { 
                showErrorDialog = false
            }
        )
    }
} 