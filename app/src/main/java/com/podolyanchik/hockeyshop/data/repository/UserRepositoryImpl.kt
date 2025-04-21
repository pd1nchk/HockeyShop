package com.podolyanchik.hockeyshop.data.repository

import com.podolyanchik.hockeyshop.data.local.dao.UserDao
import com.podolyanchik.hockeyshop.data.local.entity.CurrentUserEntity
import com.podolyanchik.hockeyshop.data.local.entity.UserEntity
import com.podolyanchik.hockeyshop.domain.model.User
import com.podolyanchik.hockeyshop.domain.model.UserRole
import com.podolyanchik.hockeyshop.domain.repository.UserRepository
import com.podolyanchik.hockeyshop.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    
    override suspend fun registerUser(name: String, email: String, password: String, isAdmin: Boolean): Resource<User> {
        return try {
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                return Resource.Error("Пользователь с таким email уже существует")
            }
            
            val userId = UUID.randomUUID().toString()
            val passwordHash = hashPassword(password)
            val role = if (isAdmin) UserRole.ADMIN.name else UserRole.USER.name
            
            val userEntity = UserEntity(
                id = userId,
                name = name,
                email = email,
                passwordHash = passwordHash,
                role = role
            )
            
            userDao.insertUser(userEntity)
            
            val user = userEntity.toUser()
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error("Произошла ошибка при регистрации: ${e.localizedMessage}")
        }
    }
    
    override suspend fun loginUser(email: String, password: String): Resource<User> {
        return try {
            val userEntity = userDao.getUserByEmail(email)
                ?: return Resource.Error("Пользователь с таким email не найден")
            
            val passwordHash = hashPassword(password)
            if (userEntity.passwordHash != passwordHash) {
                return Resource.Error("Неверный пароль")
            }
            
            // Сохраняем текущего пользователя
            val currentUserEntity = CurrentUserEntity(userId = userEntity.id)
            userDao.setCurrentUser(currentUserEntity)
            
            val user = userEntity.toUser()
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error("Произошла ошибка при входе: ${e.localizedMessage}")
        }
    }
    
    override suspend fun logoutUser() {
        userDao.clearCurrentUser()
    }
    
    override suspend fun getCurrentUser(): User? {
        val currentUserEntity = userDao.getCurrentUser() ?: return null
        val userEntity = userDao.getUserById(currentUserEntity.userId) ?: return null
        return userEntity.toUser()
    }
    
    override fun observeCurrentUser(): Flow<User?> {
        return userDao.observeCurrentUser().map { userEntity ->
            userEntity?.toUser()
        }
    }
    
    override suspend fun updateUserProfile(user: User): Resource<User> {
        return try {
            val currentUserEntity = userDao.getCurrentUser()
                ?: return Resource.Error("Пользователь не авторизован")
            
            val userEntity = userDao.getUserById(currentUserEntity.userId)
                ?: return Resource.Error("Пользователь не найден")
            
            val updatedUserEntity = userEntity.copy(
                name = user.name,
                photoUrl = user.photoUrl,
                phone = user.phone,
                address = user.address,
                paymentMethod = user.paymentMethod
            )
            
            userDao.updateUser(updatedUserEntity)
            
            Resource.Success(updatedUserEntity.toUser())
        } catch (e: Exception) {
            Resource.Error("Произошла ошибка при обновлении профиля: ${e.localizedMessage}")
        }
    }
    
    override suspend fun updateUserPassword(oldPassword: String, newPassword: String): Resource<Unit> {
        return try {
            val currentUserEntity = userDao.getCurrentUser()
                ?: return Resource.Error("Пользователь не авторизован")
            
            val userEntity = userDao.getUserById(currentUserEntity.userId)
                ?: return Resource.Error("Пользователь не найден")
            
            val oldPasswordHash = hashPassword(oldPassword)
            if (userEntity.passwordHash != oldPasswordHash) {
                return Resource.Error("Неверный текущий пароль")
            }
            
            val newPasswordHash = hashPassword(newPassword)
            val updatedUserEntity = userEntity.copy(passwordHash = newPasswordHash)
            
            userDao.updateUser(updatedUserEntity)
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Произошла ошибка при смене пароля: ${e.localizedMessage}")
        }
    }
    
    override suspend fun forgotPassword(email: String): Resource<Unit> {
        // В реальном приложении здесь была бы логика для отправки ссылки на сброс пароля
        // В демо-версии просто проверяем существование пользователя
        return try {
            val userEntity = userDao.getUserByEmail(email)
                ?: return Resource.Error("Пользователь с таким email не найден")
            
            // Имитация успешной отправки письма
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Произошла ошибка: ${e.localizedMessage}")
        }
    }
    
    override suspend fun getUserById(userId: String): Resource<User> {
        return try {
            val userEntity = userDao.getUserById(userId)
                ?: return Resource.Error("Пользователь не найден")
            
            Resource.Success(userEntity.toUser())
        } catch (e: Exception) {
            Resource.Error("Произошла ошибка: ${e.localizedMessage}")
        }
    }
    
    override fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers().map { userEntities ->
            userEntities.map { it.toUser() }
        }
    }
    
    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
    
    private fun UserEntity.toUser(): User {
        return User(
            id = id,
            name = name,
            email = email,
            role = UserRole.valueOf(role),
            photoUrl = photoUrl,
            phone = phone,
            address = address,
            paymentMethod = paymentMethod
        )
    }
} 