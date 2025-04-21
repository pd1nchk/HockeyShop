package com.podolyanchik.hockeyshop.domain.use_case.auth

import com.podolyanchik.hockeyshop.domain.repository.UserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        userRepository.logoutUser()
    }
} 