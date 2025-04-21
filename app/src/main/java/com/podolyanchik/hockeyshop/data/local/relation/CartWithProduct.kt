package com.podolyanchik.hockeyshop.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.podolyanchik.hockeyshop.data.local.entity.CartEntity
import com.podolyanchik.hockeyshop.data.local.entity.ProductEntity

/**
 * Relation class that combines a CartEntity with its associated ProductEntity
 * This allows us to easily retrieve both cart items and their product details in one query
 */
data class CartWithProduct(
    @Embedded val cartItem: CartEntity,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: ProductEntity
) 