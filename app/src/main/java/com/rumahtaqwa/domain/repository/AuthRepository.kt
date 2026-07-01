package com.rumahtaqwa.domain.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    val isLoggedIn: Boolean
    suspend fun loginWithEmail(email: String, password: String): Result<FirebaseUser>
    suspend fun registerWithEmail(name: String, email: String, password: String): Result<FirebaseUser>
    suspend fun logout()
}