package com.podolyanchik.hockeyshop.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.podolyanchik.hockeyshop.data.local.entity.OrderItemEntity
import com.podolyanchik.hockeyshop.data.local.entity.ProductEntity

// This class is used for Room to understand the relationship between OrderItem and Product
// It's a POJO (Plain Old Java Object) for query results, not an entity itself
data class OrderItemWithProduct(
    @Embedded val orderItem: OrderItemEntity,
    
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: ProductEntity
) 