package com.podolyanchik.hockeyshop.data.repository

import com.podolyanchik.hockeyshop.data.local.dao.OrderDao
import com.podolyanchik.hockeyshop.data.local.dao.ProductDao
import com.podolyanchik.hockeyshop.data.local.dao.UserDao
import com.podolyanchik.hockeyshop.data.local.dao.CategoryDao
import com.podolyanchik.hockeyshop.data.local.entity.OrderEntity
import com.podolyanchik.hockeyshop.data.local.entity.OrderItemEntity
import com.podolyanchik.hockeyshop.data.local.entity.ProductEntity
import com.podolyanchik.hockeyshop.data.local.relation.OrderWithItems
import com.podolyanchik.hockeyshop.domain.model.Order
import com.podolyanchik.hockeyshop.domain.model.OrderItem
import com.podolyanchik.hockeyshop.domain.model.OrderStatus
import com.podolyanchik.hockeyshop.domain.model.Product
import com.podolyanchik.hockeyshop.domain.model.Category
import com.podolyanchik.hockeyshop.domain.repository.OrderRepository
import com.podolyanchik.hockeyshop.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.flow

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao,
    private val productDao: ProductDao,
    private val userDao: UserDao,
    private val categoryDao: CategoryDao
) : OrderRepository {

    override suspend fun createOrder(order: Order): Resource<Order> {
        return try {
            // Создаем сущность заказа для базы данных
            val orderEntity = OrderEntity(
                id = order.id,
                userId = order.userId,
                userName = order.userName,
                userEmail = order.userEmail,
                userPhone = order.userPhone,
                userAddress = order.userAddress,
                status = order.status.name,
                total = order.totalPrice,
                shippingCost = order.shippingCost,
                createdAt = order.createdAt.time,
                completedAt = order.completedAt?.time,
                deliveryAddress = order.userAddress ?: "",
                paymentMethod = "Unknown" // Предполагаем, что вам нужно добавить это поле в модель Order
            )
            
            // Сохраняем заказ в базе данных
            orderDao.insertOrder(orderEntity)
            
            // Создаем сущности элементов заказа
            val orderItemEntities = order.items.map { item ->
                OrderItemEntity(
                    orderId = order.id,
                    productId = item.product.id,
                    quantity = item.quantity,
                    pricePerItem = item.pricePerItem
                )
            }
            
            // Сохраняем элементы заказа в базе данных
            orderDao.insertOrderItems(orderItemEntities)
            
            Resource.Success(order)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Произошла ошибка при создании заказа")
        }
    }

    override suspend fun getOrdersByUserId(userId: String): Flow<Resource<List<Order>>> = flow {
        emit(Resource.Loading())
        try {
            orderDao.getUserOrders(userId).collect { orderEntities ->
                val orders = orderEntities.map { it.toOrder() }
                emit(Resource.Success(orders))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Ошибка при получении заказов пользователя"))
        }
    }

    override suspend fun getOrdersByStatus(status: OrderStatus): Flow<Resource<List<Order>>> = flow {
        emit(Resource.Loading())
        try {
            orderDao.getOrdersByStatus(status.name).collect { orderEntities ->
                val orders = orderEntities.map { it.toOrder() }
                emit(Resource.Success(orders))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Ошибка при получении заказов по статусу"))
        }
    }

    override suspend fun getOrderById(id: String): Flow<Resource<Order>> = flow {
        emit(Resource.Loading())
        try {
            orderDao.getOrderWithItems(id).collect { orderWithItems ->
                if (orderWithItems != null) {
                    val order = orderWithItems.toOrder()
                    emit(Resource.Success(order))
                } else {
                    emit(Resource.Error("Заказ не найден"))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Ошибка при получении заказа"))
        }
    }

    override suspend fun getAllOrders(): Flow<Resource<List<Order>>> = flow {
        emit(Resource.Loading())
        try {
            orderDao.getAllOrders().collect { orderEntities ->
                val orders = orderEntities.map { it.toOrder() }
                emit(Resource.Success(orders))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Ошибка при получении всех заказов"))
        }
    }

    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Resource<Order> {
        return try {
            // Обновляем статус заказа в базе данных
            val completedAt = if (status == OrderStatus.COMPLETED) System.currentTimeMillis() else null
            orderDao.updateOrderStatus(orderId, status.name, completedAt)
            
            // Получаем обновленный заказ
            val orderEntity = orderDao.getOrderById(orderId)
            if (orderEntity != null) {
                Resource.Success(orderEntity.toOrder())
            } else {
                Resource.Error("Заказ не найден после обновления")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка при обновлении статуса заказа")
        }
    }
    
    // Вспомогательный метод для преобразования OrderWithItems в Order
    private suspend fun OrderWithItems.toOrder(): Order {
        val orderItems = mutableListOf<OrderItem>()
        
        for (itemEntity in items) {
            val productEntity = productDao.getProductById(itemEntity.productId)
                ?: throw Exception("Продукт не найден")
                
            val categoryEntity = categoryDao.getCategoryById(productEntity.categoryId)
                ?: throw Exception("Категория не найдена")
                
            val category = Category(
                id = categoryEntity.id,
                name = categoryEntity.name,
                iconUrl = categoryEntity.iconUrl,
                description = categoryEntity.description
            )
            
            val product = Product(
                id = productEntity.id,
                name = productEntity.name,
                description = productEntity.description,
                price = productEntity.price,
                imageUrl = productEntity.imageUrl,
                category = category,
                quantity = productEntity.quantity,
                rating = productEntity.rating,
                discount = productEntity.discount,
                additionalImages = productEntity.additionalImages,
                isPopular = productEntity.isPopular,
                isNew = productEntity.isNew
            )
            
            orderItems.add(
                OrderItem(
                    product = product,
                    quantity = itemEntity.quantity,
                    pricePerItem = itemEntity.pricePerItem
                )
            )
        }
        
        return Order(
            id = order.id,
            userId = order.userId,
            userName = order.userName,
            userEmail = order.userEmail,
            userPhone = order.userPhone,
            userAddress = order.userAddress,
            items = orderItems,
            totalPrice = order.total,
            shippingCost = order.shippingCost,
            status = OrderStatus.valueOf(order.status),
            createdAt = Date(order.createdAt),
            completedAt = order.completedAt?.let { Date(it) }
        )
    }
    
    // Вспомогательный метод для преобразования сущности заказа в доменную модель
    private suspend fun OrderEntity.toOrder(): Order {
        // Используем orderDao.getOrderById для получения базовой информации о заказе
        // Получаем элементы заказа напрямую из базы данных
        val orderItems = mutableListOf<OrderItem>()
        
        try {
            // Получаем элементы заказа напрямую из базы данных
            val orderItemEntities = orderDao.getOrderItemsDirectly(id)
            
            // Обрабатываем каждый элемент заказа
            for (itemEntity in orderItemEntities) {
                val productEntity = productDao.getProductById(itemEntity.productId)
                    ?: throw Exception("Продукт не найден")
                    
                val categoryEntity = categoryDao.getCategoryById(productEntity.categoryId)
                    ?: throw Exception("Категория не найдена")
                    
                val category = Category(
                    id = categoryEntity.id,
                    name = categoryEntity.name,
                    iconUrl = categoryEntity.iconUrl,
                    description = categoryEntity.description
                )
                
                val product = Product(
                    id = productEntity.id,
                    name = productEntity.name,
                    description = productEntity.description,
                    price = productEntity.price,
                    imageUrl = productEntity.imageUrl,
                    category = category,
                    quantity = productEntity.quantity,
                    rating = productEntity.rating,
                    discount = productEntity.discount,
                    additionalImages = productEntity.additionalImages,
                    isPopular = productEntity.isPopular,
                    isNew = productEntity.isNew
                )
                
                orderItems.add(
                    OrderItem(
                        product = product,
                        quantity = itemEntity.quantity,
                        pricePerItem = itemEntity.pricePerItem
                    )
                )
            }
        } catch (e: Exception) {
            // В случае ошибки логируем и возвращаем пустой список элементов
            e.printStackTrace()
        }
        
        return Order(
            id = id,
            userId = userId,
            userName = userName,
            userEmail = userEmail,
            userPhone = userPhone,
            userAddress = userAddress,
            items = orderItems,
            totalPrice = total,
            shippingCost = shippingCost,
            status = OrderStatus.valueOf(status),
            createdAt = Date(createdAt),
            completedAt = completedAt?.let { Date(it) }
        )
    }
} 