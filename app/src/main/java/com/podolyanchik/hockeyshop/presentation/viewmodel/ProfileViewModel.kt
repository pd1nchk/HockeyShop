package com.podolyanchik.hockeyshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podolyanchik.hockeyshop.domain.model.User
import com.podolyanchik.hockeyshop.domain.model.UserRole
import com.podolyanchik.hockeyshop.domain.repository.UserRepository
import com.podolyanchik.hockeyshop.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _updateState = MutableStateFlow<Resource<User>>(Resource.Initial())
    val updateState: StateFlow<Resource<User>> = _updateState.asStateFlow()
    
    // Состояние для смены пароля
    private val _passwordChangeState = MutableStateFlow<Resource<Unit>>(Resource.Initial())
    val passwordChangeState: StateFlow<Resource<Unit>> = _passwordChangeState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            userRepository.getCurrentUser()?.let {
                _currentUser.value = it
            }
        }
    }

    fun updateProfile(
        name: String,
        email: String,
        phone: String?,
        address: String?,
        paymentMethod: String?
    ) {
        viewModelScope.launch {
            _updateState.value = Resource.Loading()

            val result = userRepository.updateUserProfile(
                User(
                    id = "",
                    name = name,
                    email = email,
                    phone = phone,
                    address = address,
                    role = UserRole.USER,
                    paymentMethod = paymentMethod
                )
            )

            _updateState.value = result
        }
    }
    
    // Функция для изменения пароля
    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _passwordChangeState.value = Resource.Loading()
            
            if (newPassword.length < 6) {
                _passwordChangeState.value = Resource.Error("Пароль должен содержать не менее 6 символов")
                return@launch
            }
            
            val result = userRepository.updateUserPassword(currentPassword, newPassword)
            _passwordChangeState.value = result
        }
    }
    
    // Сбросить состояние изменения пароля
    fun resetPasswordChangeState() {
        _passwordChangeState.value = Resource.Initial()
    }

    fun resetState() {
        _updateState.value = Resource.Initial()
    }
} 