package com.podolyanchik.hockeyshop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.podolyanchik.hockeyshop.data.local.entity.OrderEntity
import com.podolyanchik.hockeyshop.data.local.entity.OrderItemEntity
import com.podolyanchik.hockeyshop.data.local.relation.OrderItemWithProduct
import com.podolyanchik.hockeyshop.data.local.relation.OrderWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(orderItems: List<OrderItemEntity>)
    
    @Update
    suspend fun updateOrder(order: OrderEntity)
    
    @Query("SELECT * FROM orders WHERE userId = :userId")
    fun getUserOrders(userId: String): Flow<List<OrderEntity>>
    
    @Query("SELECT * FROM orders")
    fun getAllOrders(): Flow<List<OrderEntity>>
    
    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderWithItems(orderId: String): Flow<OrderWithItems?>
    
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>>
    
    @Transaction
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItemsWithProducts(orderId: String): Flow<List<OrderItemWithProduct>>
    
    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)
} 