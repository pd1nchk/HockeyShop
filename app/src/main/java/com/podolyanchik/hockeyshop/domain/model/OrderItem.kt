package com.podolyanchik.hockeyshop.domain.model

data class OrderItem(
    val product: Product,
    val quantity: Int,
    val pricePerItem: Double
) {
    val totalPrice: Double
        get() = pricePerItem * quantity
} 