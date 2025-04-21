package com.podolyanchik.hockeyshop.domain.use_case.auth

import com.podolyanchik.hockeyshop.domain.model.User
import com.podolyanchik.hockeyshop.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<User?> {
        return userRepository.observeCurrentUser()
    }
} 