package com.podolyanchik.hockeyshop.domain.repository

import com.podolyanchik.hockeyshop.domain.model.Order
import com.podolyanchik.hockeyshop.domain.model.OrderStatus
import com.podolyanchik.hockeyshop.util.Resource
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    
    fun getUserOrders(): Flow<List<Order>>
    
    fun getAllOrders(): Flow<List<Order>>
    
    fun getOrderDetails(orderId: String): Flow<Order?>
    
    suspend fun placeOrder(deliveryAddress: String, paymentMethod: String): Resource<Order>
    
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Resource<Unit>
    
    suspend fun cancelOrder(orderId: String): Resource<Unit>
} 