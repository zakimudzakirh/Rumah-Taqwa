package com.rumahtaqwa.ui.screens.auth

import com.rumahtaqwa.core.util.FieldState

data class AuthState(
    val isLoginMode: Boolean = true,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,

    val name: String = "",
    val email: String = "",
    val password: String = "",
    val nameState: FieldState = FieldState.Idle,
    val emailState: FieldState = FieldState.Idle,
    val passwordState: FieldState = FieldState.Idle
) {
    val isFormValid: Boolean
        get() = (isLoginMode && emailState is FieldState.Valid
                && passwordState is FieldState.Valid)
                || (!isLoginMode && nameState is FieldState.Valid
                && emailState is FieldState.Valid
                && passwordState is FieldState.Valid)
}

enum class AuthField {
    NAME, EMAIL, PASSWORD
}

object AuthValidator {
    fun validateName(value: String): FieldState {
        return when {
            value.isBlank() -> FieldState.Error("* Nama tidak boleh kosong")
            value.length < 3 -> FieldState.Error("* Nama minimal 3 karakter")
            !value.matches(Regex("^[a-zA-Z ]+$")) -> FieldState.Error("* Nama hanya boleh huruf dan spasi")
            else -> FieldState.Valid
        }
    }

    fun validateEmail(value: String): FieldState {
        return when {
            value.isBlank() -> FieldState.Error("* Email tidak boleh kosong")
            !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches() ->
                FieldState.Error("* Format email tidak valid")
            else -> FieldState.Valid
        }
    }

    fun validatePassword(value: String): FieldState {
        return when {
            value.isBlank() -> FieldState.Error("Password tidak boleh kosong")
            else -> FieldState.Valid
        }
    }
}
