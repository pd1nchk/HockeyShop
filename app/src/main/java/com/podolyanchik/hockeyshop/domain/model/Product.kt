package com.podolyanchik.hockeyshop.domain.model

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: Category,
    val quantity: Int,
    val rating: Float = 0f,
    val discount: Float = 0f,
    val finalPrice: Double = (price * (100 - discount)) / 100,
    val additionalImages: List<String> = emptyList(),
    val isPopular: Boolean = false,
    val isNew: Boolean = false
) 