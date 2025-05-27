package com.podolyanchik.hockeyshop.di

import com.podolyanchik.hockeyshop.data.repository.CategoryRepositoryImpl
import com.podolyanchik.hockeyshop.data.repository.OrderRepositoryImpl
import com.podolyanchik.hockeyshop.data.repository.ProductRepositoryImpl
import com.podolyanchik.hockeyshop.data.repository.UserRepositoryImpl
import com.podolyanchik.hockeyshop.domain.repository.CategoryRepository
import com.podolyanchik.hockeyshop.domain.repository.OrderRepository
import com.podolyanchik.hockeyshop.domain.repository.ProductRepository
import com.podolyanchik.hockeyshop.domain.repository.UserRepository
import com.podolyanchik.hockeyshop.data.repository.CartRepositoryImpl
import com.podolyanchik.hockeyshop.domain.repository.CartRepository
import com.podolyanchik.hockeyshop.data.local.dao.CartDao
import com.podolyanchik.hockeyshop.data.local.dao.ProductDao
import com.podolyanchik.hockeyshop.data.local.dao.UserDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// This module uses @Binds for repositories that follow the implementation-interface pattern
@Module
@InstallIn(SingletonComponent::class)
abstract class BindRepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
    
    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository
    
    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository
    
    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository
}

// This module uses @Provides for repositories that need more complex initialization
@Module
@InstallIn(SingletonComponent::class)
object ProvideRepositoryModule {
    
    @Provides
    @Singleton
    fun provideCartRepository(
        cartDao: CartDao,
        userDao: UserDao,
        productDao: ProductDao
    ): CartRepository {
        return CartRepositoryImpl(cartDao, userDao, productDao)
    }
} 