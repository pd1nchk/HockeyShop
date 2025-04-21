package com.podolyanchik.hockeyshop.domain.use_case.auth

import com.podolyanchik.hockeyshop.domain.model.User
import com.podolyanchik.hockeyshop.domain.repository.UserRepository
import com.podolyanchik.hockeyshop.util.Resource
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): Resource<User> {
        if (email.isBlank()) {
            return Resource.Error("Email не может быть пустым")
        }
        if (password.isBlank()) {
            return Resource.Error("Пароль не может быть пустым")
        }
        
        return userRepository.loginUser(email, password)
    }
} 