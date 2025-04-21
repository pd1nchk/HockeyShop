package com.podolyanchik.hockeyshop.data.repository

import com.podolyanchik.hockeyshop.data.local.dao.CartDao
import com.podolyanchik.hockeyshop.data.local.dao.UserDao
import com.podolyanchik.hockeyshop.data.local.entity.CartEntity
import com.podolyanchik.hockeyshop.data.mapper.toProduct
import com.podolyanchik.hockeyshop.domain.model.Product
import com.podolyanchik.hockeyshop.domain.repository.CartRepository
import com.podolyanchik.hockeyshop.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val userDao: UserDao
) : CartRepository {

    override fun getCartItems(): Flow<Resource<Map<Product, Int>>> = flow {
        emit(Resource.Loading())
        try {
            val currentUser = userDao.getCurrentUser() ?: throw Exception("User not logged in")
            cartDao.getCartItemsWithProductsForUser(currentUser.userId)
                .map { cartItems ->
                    // Convert to Map<Product, Int>
                    val productMap = cartItems.associate { 
                        it.product.toProduct() to it.cartItem.quantity
                    }
                    Resource.Success(productMap)
                }
                .collect { emit(it) }
        } catch (e: Exception) {
            emit(Resource.Error("Failed to load cart: ${e.message}"))
        }
    }

    override fun getCartItemCount(): Flow<Int> = flow {
        try {
            val currentUser = userDao.getCurrentUser() ?: throw Exception("User not logged in")
            val totalQuantity = cartDao.getTotalQuantity(currentUser.userId).firstOrNull() ?: 0
            emit(totalQuantity)
        } catch (e: Exception) {
            emit(0)
        }
    }

    override fun getCartTotal(): Flow<Double> = flow {
        try {
            val currentUser = userDao.getCurrentUser() ?: throw Exception("User not logged in")
            val cartItems = cartDao.getCartItemsWithProductsForUser(currentUser.userId).first()
            
            val total = cartItems.sumOf { cartWithProduct ->
                val price = if (cartWithProduct.product.discount > 0) {
                    cartWithProduct.product.price * (1 - cartWithProduct.product.discount / 100)
                } else {
                    cartWithProduct.product.price
                }
                price * cartWithProduct.cartItem.quantity
            }
            
            emit(total)
        } catch (e: Exception) {
            emit(0.0)
        }
    }

    override fun isProductInCart(productId: String): Flow<Boolean> = flow {
        try {
            val currentUser = userDao.getCurrentUser() ?: throw Exception("User not logged in")
            cartDao.isProductInCart(currentUser.userId, productId)
                .collect { emit(it) }
        } catch (e: Exception) {
            emit(false)
        }
    }

    override suspend fun addToCart(product: Product, quantity: Int): Resource<Unit> {
        return try {
            val currentUser = userDao.getCurrentUser() ?: throw Exception("User not logged in")
            val cartItem = CartEntity(
                userId = currentUser.userId,
                productId = product.id,
                quantity = quantity
            )
            cartDao.addCartItem(cartItem)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to add to cart: ${e.message}")
        }
    }

    override suspend fun updateQuantity(product: Product, quantity: Int): Resource<Unit> {
        return try {
            val currentUser = userDao.getCurrentUser() ?: throw Exception("User not logged in")
            val cartItem = CartEntity(
                userId = currentUser.userId,
                productId = product.id,
                quantity = quantity
            )
            cartDao.updateCartItem(cartItem)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to update quantity: ${e.message}")
        }
    }

    override suspend fun removeFromCart(product: Product): Resource<Unit> {
        return try {
            val currentUser = userDao.getCurrentUser() ?: throw Exception("User not logged in")
            cartDao.removeCartItemByIds(currentUser.userId, product.id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to remove from cart: ${e.message}")
        }
    }

    override suspend fun clearCart(): Resource<Unit> {
        return try {
            val currentUser = userDao.getCurrentUser() ?: throw Exception("User not logged in")
            cartDao.clearCartForUser(currentUser.userId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to clear cart: ${e.message}")
        }
    }
} 