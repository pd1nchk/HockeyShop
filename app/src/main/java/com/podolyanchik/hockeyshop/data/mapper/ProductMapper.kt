package com.podolyanchik.hockeyshop.data.mapper

import com.podolyanchik.hockeyshop.data.local.entity.ProductEntity
import com.podolyanchik.hockeyshop.domain.model.Category
import com.podolyanchik.hockeyshop.domain.model.Product


fun ProductEntity.toProduct(): Product {
    return Product(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrl ?: "",
        category = Category(
            id = categoryId,
            name = "", // We don't have the category name in ProductEntity, it could be fetched if needed
            description = "",
            iconUrl = ""
        ),
        quantity = quantity,
        discount = discount,
        isPopular = isPopular,
        isNew = isNew,
        additionalImages = additionalImages
    )
}
