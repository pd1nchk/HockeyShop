package com.podolyanchik.hockeyshop.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "order_items",
    primaryKeys = ["orderId", "productId"],
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("orderId"), Index("productId")]
)
data class OrderItemEntity(
    val orderId: String,
    val productId: String,
    val quantity: Int,
    val pricePerItem: Double
) 