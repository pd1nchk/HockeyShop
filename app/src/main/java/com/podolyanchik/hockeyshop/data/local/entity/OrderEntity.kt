package com.podolyanchik.hockeyshop.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class OrderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val status: String, // PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    val total: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val deliveryAddress: String,
    val paymentMethod: String
) 