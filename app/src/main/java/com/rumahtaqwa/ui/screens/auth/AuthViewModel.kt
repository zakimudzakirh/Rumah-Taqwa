package com.rumahtaqwa.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.rumahtaqwa.core.util.FieldState
import com.rumahtaqwa.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    fun switchMode() {
        _state.update {
            it.copy(
                isLoginMode = !it.isLoginMode
            )
        }
    }

    fun onChangeValue(field: AuthField, value: String) {
        _state.update { current ->
            when (field) {
                AuthField.NAME -> current.copy(
                    name = value,
                    nameState = if (value.isBlank()) FieldState.Idle
                    else AuthValidator.validateName(value)
                )
                AuthField.EMAIL -> current.copy(
                    email = value,
                    emailState = if (value.isBlank()) FieldState.Idle
                    else AuthValidator.validateEmail(value)
                )
                AuthField.PASSWORD -> current.copy(
                    password = value,
                    passwordState = if (value.isBlank()) FieldState.Idle
                    else AuthValidator.validatePassword(value)
                )
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val email = _state.value.email
            val password = _state.value.password
            val result = authUseCase.login(email, password)
            _state.update {
                it.copy(
                    isLoading = false,
                    isSuccess = result.isSuccess,
                    error = result.exceptionOrNull()?.toAuthMessage()
                )
            }
        }
    }

    fun register() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val name = _state.value.name
            val email = _state.value.email
            val password = _state.value.password
            val result = authUseCase.register(name, email, password)
            _state.update {
                it.copy(
                    isLoading = false,
                    isSuccess = result.isSuccess,
                    error = result.exceptionOrNull()?.toAuthMessage()
                )
            }
        }
    }
}

private fun Throwable.toAuthMessage(): String = when (this) {
    is FirebaseAuthInvalidCredentialsException -> "Email atau password salah"
    is FirebaseAuthUserCollisionException -> "Email sudah terdaftar"
    is FirebaseAuthWeakPasswordException -> "Password minimal 6 karakter"
    is FirebaseNetworkException -> "Tidak ada koneksi internet"
    else -> "Terjadi kesalahan, coba lagi"
}