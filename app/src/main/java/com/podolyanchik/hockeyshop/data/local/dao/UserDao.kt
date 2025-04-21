package com.podolyanchik.hockeyshop.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.podolyanchik.hockeyshop.data.local.entity.CurrentUserEntity
import com.podolyanchik.hockeyshop.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE role = 'ADMIN'")
    fun getAllAdmins(): Flow<List<UserEntity>>
    
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>
    
    // CurrentUser operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCurrentUser(currentUser: CurrentUserEntity)
    
    @Query("SELECT * FROM current_user LIMIT 1")
    suspend fun getCurrentUser(): CurrentUserEntity?
    
    @Query("DELETE FROM current_user")
    suspend fun clearCurrentUser()
    
    @Query("SELECT u.* FROM users u INNER JOIN current_user cu ON u.id = cu.userId LIMIT 1")
    fun observeCurrentUser(): Flow<UserEntity?>
} 