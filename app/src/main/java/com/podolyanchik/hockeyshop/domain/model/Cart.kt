package com.podolyanchik.hockeyshop.domain.model

/**
 * Модель данных для корзины покупок
 * @property id уникальный идентификатор корзины
 * @property userId идентификатор пользователя, которому принадлежит корзина
 * @property items список товаров в корзине
 * @property totalPrice общая стоимость товаров в корзине
 */
data class Cart(
    val id: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0
) 