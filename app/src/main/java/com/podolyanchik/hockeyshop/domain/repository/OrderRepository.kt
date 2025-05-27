package com.podolyanchik.hockeyshop.domain.repository

import com.podolyanchik.hockeyshop.domain.model.Order
import com.podolyanchik.hockeyshop.domain.model.OrderStatus
import com.podolyanchik.hockeyshop.util.Resource
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    /**
     * Create a new order
     */
    suspend fun createOrder(order: Order): Resource<Order>
    
    /**
     * Get orders by user ID
     */
    suspend fun getOrdersByUserId(userId: String): Flow<Resource<List<Order>>>
    
    /**
     * Get orders by status
     */
    suspend fun getOrdersByStatus(status: OrderStatus): Flow<Resource<List<Order>>>
    
    /**
     * Get order by ID
     */
    suspend fun getOrderById(id: String): Flow<Resource<Order>>
    
    /**
     * Get all orders
     */
    suspend fun getAllOrders(): Flow<Resource<List<Order>>>
    
    /**
     * Update order status
     */
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Resource<Order>
} 