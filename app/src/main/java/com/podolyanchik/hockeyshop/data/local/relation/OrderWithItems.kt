package com.podolyanchik.hockeyshop.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.podolyanchik.hockeyshop.data.local.entity.OrderEntity
import com.podolyanchik.hockeyshop.data.local.entity.OrderItemEntity

// This class defines a one-to-many relationship between Order and OrderItems
data class OrderWithItems(
    @Embedded val order: OrderEntity,
    
    @Relation(
        entity = OrderItemEntity::class,
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val items: List<OrderItemEntity>
) 