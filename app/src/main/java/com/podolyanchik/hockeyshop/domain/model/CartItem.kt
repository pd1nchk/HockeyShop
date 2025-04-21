package com.podolyanchik.hockeyshop.domain.model

/**
 * Модель данных для товара в корзине
 * @property id уникальный идентификатор элемента корзины
 * @property productId идентификатор товара
 * @property productName название товара
 * @property productImage ссылка на изображение товара
 * @property price цена товара
 * @property quantity количество товара в корзине
 */
data class CartItem(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val productImage: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1
) {
    val totalPrice: Double
        get() = price * quantity
} 