package com.podolyanchik.hockeyshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podolyanchik.hockeyshop.domain.model.Order
import com.podolyanchik.hockeyshop.domain.model.OrderItem
import com.podolyanchik.hockeyshop.domain.model.OrderStatus
import com.podolyanchik.hockeyshop.domain.model.User
import com.podolyanchik.hockeyshop.domain.repository.OrderRepository
import com.podolyanchik.hockeyshop.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    // Active orders for user or all active orders for admin
    private val _activeOrders = MutableStateFlow<List<Order>>(emptyList())
    val activeOrders: StateFlow<List<Order>> = _activeOrders.asStateFlow()

    // Completed orders for user or all completed orders for admin
    private val _completedOrders = MutableStateFlow<List<Order>>(emptyList())
    val completedOrders: StateFlow<List<Order>> = _completedOrders.asStateFlow()

    // Selected order for details
    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Create a new order from cart items
     */
    fun createOrderFromCart(
        cartItems: Map<com.podolyanchik.hockeyshop.domain.model.Product, Int>,
        currentUser: User,
        shippingCost: Double
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Convert cart items to order items
                val orderItems = cartItems.map { (product, quantity) ->
                    OrderItem(
                        product = product,
                        quantity = quantity,
                        pricePerItem = product.price * (1 - product.discount / 100)
                    )
                }

                // Calculate total price
                val totalPrice = orderItems.sumOf { it.pricePerItem * it.quantity }

                // Create new order
                val newOrder = Order(
                    id = UUID.randomUUID().toString(),
                    userId = currentUser.id,
                    userName = currentUser.name,
                    userEmail = currentUser.email,
                    userPhone = currentUser.phone,
                    userAddress = currentUser.address,
                    items = orderItems,
                    totalPrice = totalPrice,
                    shippingCost = shippingCost,
                    status = OrderStatus.ACTIVE,
                    createdAt = Date()
                )

                val result = orderRepository.createOrder(newOrder)
                _isLoading.value = false

                if (result is Resource.Error) {
                    _error.value = result.message
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message ?: "An error occurred when creating order"
            }
        }
    }

    /**
     * Load orders for a specific user
     */
    fun loadUserOrders(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                orderRepository.getOrdersByUserId(userId)
                    .onEach { result ->
                        _isLoading.value = false

                        when (result) {
                            is Resource.Success -> {
                                val orders = result.data ?: emptyList()
                                _activeOrders.value = orders.filter { it.status == OrderStatus.ACTIVE }
                                _completedOrders.value = orders.filter { it.status == OrderStatus.COMPLETED }
                            }
                            is Resource.Error -> {
                                _error.value = result.message
                            }
                            else -> {} // No special handling for other states
                        }
                    }
                    .launchIn(viewModelScope)
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Error loading orders: ${e.localizedMessage ?: e.toString()}"
            }
        }
    }

    /**
     * Load all orders (admin only)
     */
    fun loadAllOrders() {
        viewModelScope.launch {
            _isLoading.value = true

            orderRepository.getAllOrders()
                .onEach { result ->
                    _isLoading.value = false

                    when (result) {
                        is Resource.Success -> {
                            val orders = result.data ?: emptyList()
                            _activeOrders.value = orders.filter { it.status == OrderStatus.ACTIVE }
                            _completedOrders.value = orders.filter { it.status == OrderStatus.COMPLETED }
                        }
                        is Resource.Error -> {
                            _error.value = result.message
                        }
                        else -> {} // No special handling for other states
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    /**
     * Set selected order for displaying order details
     */
    fun selectOrder(order: Order) {
        _selectedOrder.value = order
    }

    /**
     * Clear selected order
     */
    fun clearSelectedOrder() {
        _selectedOrder.value = null
    }

    /**
     * Complete order (admin only)
     */
    fun completeOrder(orderId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = orderRepository.updateOrderStatus(orderId, OrderStatus.COMPLETED)
            _isLoading.value = false

            if (result is Resource.Error) {
                _error.value = result.message
            }
        }
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Get order details by ID
     */
    fun getOrderById(orderId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                orderRepository.getOrderById(orderId)
                    .onEach { result ->
                        _isLoading.value = false

                        when (result) {
                            is Resource.Success -> {
                                result.data?.let { order ->
                                    _selectedOrder.value = order
                                }
                            }
                            is Resource.Error -> {
                                _error.value = result.message
                            }
                            else -> {} // No special handling for other states
                        }
                    }
                    .launchIn(viewModelScope)
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Error loading order details: ${e.localizedMessage ?: e.toString()}"
            }
        }
    }
} 