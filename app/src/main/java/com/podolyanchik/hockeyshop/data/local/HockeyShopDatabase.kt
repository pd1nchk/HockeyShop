package com.podolyanchik.hockeyshop.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.podolyanchik.hockeyshop.data.local.converter.Converters
import com.podolyanchik.hockeyshop.data.local.dao.CartDao
import com.podolyanchik.hockeyshop.data.local.dao.CategoryDao
import com.podolyanchik.hockeyshop.data.local.dao.OrderDao
import com.podolyanchik.hockeyshop.data.local.dao.ProductDao
import com.podolyanchik.hockeyshop.data.local.dao.UserDao
import com.podolyanchik.hockeyshop.data.local.entity.CartEntity
import com.podolyanchik.hockeyshop.data.local.entity.CategoryEntity
import com.podolyanchik.hockeyshop.data.local.entity.CurrentUserEntity
import com.podolyanchik.hockeyshop.data.local.entity.OrderEntity
import com.podolyanchik.hockeyshop.data.local.entity.OrderItemEntity
import com.podolyanchik.hockeyshop.data.local.entity.ProductEntity
import com.podolyanchik.hockeyshop.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        CategoryEntity::class,
        CartEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        CurrentUserEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HockeyShopDatabase : RoomDatabase() {
    
    abstract val userDao: UserDao
    abstract val productDao: ProductDao
    abstract val categoryDao: CategoryDao
    abstract val cartDao: CartDao
    abstract val orderDao: OrderDao
    
    companion object {
        const val DATABASE_NAME = "hockey_shop_db"
    }
} 