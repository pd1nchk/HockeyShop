package com.podolyanchik.hockeyshop.domain.use_case.auth

import com.podolyanchik.hockeyshop.domain.model.User
import com.podolyanchik.hockeyshop.domain.repository.UserRepository
import com.podolyanchik.hockeyshop.util.Resource
import java.util.regex.Pattern
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        isAdmin: Boolean = false
    ): Resource<User> {
        if (name.isBlank()) {
            return Resource.Error("Имя не может быть пустым")
        }
        
        if (email.isBlank()) {
            return Resource.Error("Email не может быть пустым")
        }
        
        if (!isValidEmail(email)) {
            return Resource.Error("Некорректный формат email")
        }
        
        if (password.isBlank()) {
            return Resource.Error("Пароль не может быть пустым")
        }
        
        if (password.length < 6) {
            return Resource.Error("Пароль должен содержать не менее 6 символов")
        }
        
        if (password != confirmPassword) {
            return Resource.Error("Пароли не совпадают")
        }
        
        return userRepository.registerUser(name, email, password, isAdmin)
    }
    
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "[a-zA-Z0-9+._%\\-]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        return emailPattern.matcher(email).matches()
    }
} 