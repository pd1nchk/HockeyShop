package com.podolyanchik.hockeyshop.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val photoUrl: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val paymentMethod: String? = null
)

enum class UserRole {
    USER, ADMIN
} 