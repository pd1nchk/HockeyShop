package com.podolyanchik.hockeyshop.domain.repository

import com.podolyanchik.hockeyshop.domain.model.User
import com.podolyanchik.hockeyshop.util.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    
    suspend fun registerUser(name: String, email: String, password: String, isAdmin: Boolean): Resource<User>
    
    suspend fun loginUser(email: String, password: String): Resource<User>
    
    suspend fun logoutUser()
    
    suspend fun getCurrentUser(): User?
    
    fun observeCurrentUser(): Flow<User?>
    
    suspend fun updateUserProfile(user: User): Resource<User>
    
    suspend fun updateUserPassword(oldPassword: String, newPassword: String): Resource<Unit>
    
    suspend fun forgotPassword(email: String): Resource<Unit>
    
    suspend fun getUserById(userId: String): Resource<User>
    
    fun getAllUsers(): Flow<List<User>>
} 