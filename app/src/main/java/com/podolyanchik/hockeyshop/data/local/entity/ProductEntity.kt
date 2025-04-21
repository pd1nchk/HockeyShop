package com.podolyanchik.hockeyshop.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val categoryId: Int,
    val quantity: Int,
    val rating: Float = 0f,
    val discount: Float = 0f,
    val additionalImages: List<String> = emptyList(),
    val isPopular: Boolean = false,
    val isNew: Boolean = false
) 