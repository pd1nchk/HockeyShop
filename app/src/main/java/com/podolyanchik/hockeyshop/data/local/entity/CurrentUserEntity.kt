package com.podolyanchik.hockeyshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_user")
data class CurrentUserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val lastLoginTime: Long = System.currentTimeMillis()
) 