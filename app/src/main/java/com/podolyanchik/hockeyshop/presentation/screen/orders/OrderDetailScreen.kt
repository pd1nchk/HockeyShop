package com.podolyanchik.hockeyshop.presentation.screen.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.podolyanchik.hockeyshop.R
import com.podolyanchik.hockeyshop.domain.model.Order
import com.podolyanchik.hockeyshop.domain.model.OrderStatus
import com.podolyanchik.hockeyshop.domain.model.UserRole
import com.podolyanchik.hockeyshop.presentation.component.ErrorDialog
import com.podolyanchik.hockeyshop.presentation.component.HockeyShopButton
import com.podolyanchik.hockeyshop.presentation.component.OrderItemRow
import com.podolyanchik.hockeyshop.presentation.viewmodel.OrderViewModel
import com.podolyanchik.hockeyshop.presentation.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: String,
    onNavigateBack: () -> Unit,
    viewModel: OrderViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val selectedOrder by viewModel.selectedOrder.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val currentUser by profileViewModel.currentUser.collectAsStateWithLifecycle()
    
    var showErrorDialog by remember { mutableStateOf(false) }
    var showConfirmCompleteDialog by remember { mutableStateOf(false) }
    
    // Fetch order details
    LaunchedEffect(orderId) {
        viewModel.getOrderById(orderId)
    }
    
    LaunchedEffect(error) {
        if (error != null) {
            showErrorDialog = true
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.details_order)) },
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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (selectedOrder == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.order_not_found),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            selectedOrder?.let { order ->
                OrderDetailContent(
                    order = order,
                    isAdmin = currentUser?.role == UserRole.ADMIN,
                    onCompleteOrder = {
                        showConfirmCompleteDialog = true
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
    
    // Show error dialog if there's an error
    if (showErrorDialog && error != null) {
        ErrorDialog(
            message = error ?: "",
            onDismiss = { 
                showErrorDialog = false
                viewModel.clearError()
            }
        )
    }
    
    // Confirmation dialog for completing an order (admin only)
    if (showConfirmCompleteDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmCompleteDialog = false },
            title = { Text(text = stringResource(R.string.complete_order_title)) },
            text = { Text(text = stringResource(R.string.complete_order_message)) },
            confirmButton = {
                HockeyShopButton(
                    text = stringResource(R.string.complete),
                    onClick = {
                        selectedOrder?.let { order ->
                            viewModel.completeOrder(order.id)
                        }
                        showConfirmCompleteDialog = false
                    }
                )
            },
            dismissButton = {
                HockeyShopButton(
                    text = stringResource(R.string.cancel),
                    onClick = { showConfirmCompleteDialog = false },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        )
    }
}

@Composable
fun OrderDetailContent(
    order: Order,
    isAdmin: Boolean,
    onCompleteOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Order header information
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.order_number, order.id.take(8)),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = stringResource(R.string.created_on, dateFormat.format(order.createdAt)),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    if (order.status == OrderStatus.COMPLETED && order.completedAt != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.completed_on, dateFormat.format(order.completedAt)),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val statusColor = when (order.status) {
                        OrderStatus.ACTIVE -> MaterialTheme.colorScheme.primary
                        OrderStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
                    }
                    
                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = when (order.status) {
                                OrderStatus.ACTIVE -> stringResource(R.string.status_active)
                                OrderStatus.COMPLETED -> stringResource(R.string.status_completed)
                            },
                            color = statusColor,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Customer information (visible only for admins)
            if (isAdmin) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.customer_information),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = stringResource(R.string.customer_name, order.userName),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = stringResource(R.string.customer_email, order.userEmail),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        if (order.userPhone != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.customer_phone, order.userPhone),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        if (order.userAddress != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.shipping_address, order.userAddress),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Order items title
            Text(
                text = stringResource(R.string.order_items),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        // Проверим, что список элементов заказа не пустой, чтобы избежать проблем
        if (order.items.isNotEmpty()) {
            // Order items
            items(order.items) { orderItem ->
                OrderItemRow(
                    orderItem = orderItem
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            // Показываем сообщение, если заказ пуст
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет элементов в заказе",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        
        // Order summary
        item {
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.order_summary),
                        style = MaterialTheme.typography.titleMedium,
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
                            text = "$${String.format("%.2f", order.totalPrice)}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.shipping),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "$${String.format("%.2f", order.shippingCost)}",
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
                            text = "$${String.format("%.2f", order.totalPrice + order.shippingCost)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Complete order button (only for admin and active orders)
            if (isAdmin && order.status == OrderStatus.ACTIVE) {
                HockeyShopButton(
                    text = stringResource(R.string.complete_order),
                    onClick = onCompleteOrder,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
} 