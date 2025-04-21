package com.podolyanchik.hockeyshop.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "cart_items",
    primaryKeys = ["userId", "productId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("productId")]
)
data class CartItemEntity(
    val userId: String,
    val productId: String,
    val quantity: Int,
    val addedAt: Long = System.currentTimeMillis()
) 