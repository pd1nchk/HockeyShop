package com.podolyanchik.hockeyshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: String, // USER или ADMIN
    val photoUrl: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val paymentMethod: String? = null
) 