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
    val userName: String,
    val userEmail: String,
    val userPhone: String? = null,
    val userAddress: String? = null,
    val status: String, // "ACTIVE" or "COMPLETED"
    val total: Double,
    val shippingCost: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val deliveryAddress: String,
    val paymentMethod: String
) 