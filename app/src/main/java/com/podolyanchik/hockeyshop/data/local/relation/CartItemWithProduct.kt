package com.podolyanchik.hockeyshop.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.podolyanchik.hockeyshop.data.local.entity.CartItemEntity
import com.podolyanchik.hockeyshop.data.local.entity.ProductEntity

data class CartItemWithProduct(
    @Embedded val cartItem: CartItemEntity,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: ProductEntity
) 