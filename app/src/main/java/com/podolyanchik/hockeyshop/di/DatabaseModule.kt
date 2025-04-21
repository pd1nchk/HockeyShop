package com.podolyanchik.hockeyshop.di

import android.app.Application
import androidx.room.Room
import com.podolyanchik.hockeyshop.data.local.HockeyShopDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(app: Application): HockeyShopDatabase {
        return Room.databaseBuilder(
            app,
            HockeyShopDatabase::class.java,
            HockeyShopDatabase.DATABASE_NAME
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideUserDao(database: HockeyShopDatabase) = database.userDao
    
    @Provides
    @Singleton
    fun provideCategoryDao(database: HockeyShopDatabase) = database.categoryDao
    
    @Provides
    @Singleton
    fun provideProductDao(database: HockeyShopDatabase) = database.productDao
    
    @Provides
    @Singleton
    fun provideCartDao(database: HockeyShopDatabase) = database.cartDao
    
    @Provides
    @Singleton
    fun provideOrderDao(database: HockeyShopDatabase) = database.orderDao
} 