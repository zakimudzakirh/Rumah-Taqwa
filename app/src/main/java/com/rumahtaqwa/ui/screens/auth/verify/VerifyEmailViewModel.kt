package com.rumahtaqwa.ui.screens.auth.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumahtaqwa.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(
        VerifyEmailState(email = authUseCase.currentUser?.email.orEmpty())
    )
    val state = _state.asStateFlow()

    fun checkVerification() {
        viewModelScope.launch {
            _state.update { it.copy(isChecking = true, message = null) }
            authUseCase.reloadUser()
            val verified = authUseCase.isEmailVerified
            _state.update {
                it.copy(
                    isChecking = false,
                    isVerified = verified,
                    message = if (!verified) "Email belum terverifikasi. Cek inbox atau folder spam kamu." else null,
                    isMessageError = !verified
                )
            }
        }
    }

    fun resendEmail() {
        viewModelScope.launch {
            _state.update { it.copy(isResending = true, message = null) }
            val result = authUseCase.sendEmailVerification()
            _state.update {
                it.copy(
                    isResending = false,
                    message = if (result.isSuccess) "Email verifikasi sudah dikirim ulang. Jangan lupa cek folder spam juga"
                    else "Gagal mengirim email, coba lagi",
                    isMessageError = result.isFailure
                )
            }
        }
    }

    fun messageShown() {
        _state.update { it.copy(message = null) }
    }

    fun logout() {
        viewModelScope.launch {
            authUseCase.logout()
        }
    }
}
