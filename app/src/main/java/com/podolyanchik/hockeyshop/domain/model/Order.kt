package com.podolyanchik.hockeyshop.domain.model

import java.util.Date

enum class OrderStatus {
    ACTIVE,  // Текущий активный заказ
    COMPLETED  // Завершённый/закрытый заказ
}

data class Order(
    val id: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val userPhone: String?,
    val userAddress: String?,
    val items: List<OrderItem>,
    val totalPrice: Double,
    val shippingCost: Double,
    val status: OrderStatus,
    val createdAt: Date,
    val completedAt: Date? = null
) 