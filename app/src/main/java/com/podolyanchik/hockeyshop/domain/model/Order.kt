package com.podolyanchik.hockeyshop.domain.model

import java.util.Date

data class Order(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val total: Double,
    val createdAt: Date,
    val deliveryAddress: String,
    val paymentMethod: String
)

enum class OrderStatus {
    PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
} 