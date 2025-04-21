package com.podolyanchik.hockeyshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val iconUrl: String? = null,
    val description: String? = null
) 