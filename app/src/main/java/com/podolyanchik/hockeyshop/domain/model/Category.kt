package com.podolyanchik.hockeyshop.domain.model

data class Category(
    val id: Int,
    val name: String,
    val iconUrl: String? = null,
    val description: String? = null
) 