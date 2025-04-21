package com.podolyanchik.hockeyshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podolyanchik.hockeyshop.domain.model.User
import com.podolyanchik.hockeyshop.domain.use_case.auth.GetCurrentUserUseCase
import com.podolyanchik.hockeyshop.domain.use_case.auth.LoginUseCase
import com.podolyanchik.hockeyshop.domain.use_case.auth.LogoutUseCase
import com.podolyanchik.hockeyshop.domain.use_case.auth.RegisterUseCase
import com.podolyanchik.hockeyshop.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    
    private val _loginState = MutableStateFlow<Resource<User>>(Resource.Initial())
    val loginState: StateFlow<Resource<User>> = _loginState.asStateFlow()
    
    private val _registerState = MutableStateFlow<Resource<User>>(Resource.Initial())
    val registerState: StateFlow<Resource<User>> = _registerState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    init {
        observeCurrentUser()
    }
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            val result = loginUseCase(email, password)
            _loginState.value = result
        }
    }
    
    fun register(name: String, email: String, password: String, confirmPassword: String, isAdmin: Boolean = false) {
        viewModelScope.launch {
            _registerState.value = Resource.Loading()
            val result = registerUseCase(name, email, password, confirmPassword, isAdmin)
            _registerState.value = result
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _loginState.value = Resource.Loading()
            _registerState.value = Resource.Loading()
        }
    }
    
    private fun observeCurrentUser() {
        getCurrentUserUseCase().onEach { user ->
            _currentUser.value = user
        }.launchIn(viewModelScope)
    }
    
    // Сбросить состояние после навигации
    fun resetState() {
        _loginState.value = Resource.Loading()
        _registerState.value = Resource.Loading()
    }
} 