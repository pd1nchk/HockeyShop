package com.podolyanchik.hockeyshop.data.local

import com.podolyanchik.hockeyshop.data.local.dao.UserDao
import com.podolyanchik.hockeyshop.data.local.entity.UserEntity
import com.podolyanchik.hockeyshop.domain.model.UserRole
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoDataProvider @Inject constructor(
    private val userDao: UserDao
) {
    
    suspend fun initializeDemoData() {
        insertDemoUsers()
    }
    
    private suspend fun insertDemoUsers() {
        // Проверяем, есть ли уже пользователи в базе данных
        val users = userDao.getUserByEmail("admin@example.com")
        if (users == null) {
            // Добавляем демо-пользователя с ролью админа
            val adminUser = UserEntity(
                id = "admin-user-id",
                name = "Администратор",
                email = "admin@example.com",
                passwordHash = hashPassword("admin123"),
                role = UserRole.ADMIN.name,
                phone = "+7 (999) 123-45-67",
                address = "г. Москва, ул. Примерная, д. 1"
            )
            userDao.insertUser(adminUser)
            
            // Добавляем демо-пользователя с ролью обычного пользователя
            val regularUser = UserEntity(
                id = "regular-user-id",
                name = "Пользователь",
                email = "user@example.com",
                passwordHash = hashPassword("user123"),
                role = UserRole.USER.name,
                phone = "+7 (999) 765-43-21",
                address = "г. Санкт-Петербург, ул. Тестовая, д. 2"
            )
            userDao.insertUser(regularUser)
        }
    }
    
    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
} 