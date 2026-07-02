package com.rumahtaqwa.domain.usecase

import com.rumahtaqwa.domain.repository.AuthRepository
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    val currentUser get() = repository.currentUser
    val isLoggedIn get() = repository.isLoggedIn
    val isEmailVerified get() = repository.isEmailVerified

    suspend fun login(email: String, password: String) =
        repository.loginWithEmail(email, password)

    suspend fun register(name: String, email: String, password: String) =
        repository.registerWithEmail(name, email, password)

    suspend fun sendEmailVerification() = repository.sendEmailVerification()

    suspend fun reloadUser() = repository.reloadUser()

    suspend fun logout() = repository.logout()
}