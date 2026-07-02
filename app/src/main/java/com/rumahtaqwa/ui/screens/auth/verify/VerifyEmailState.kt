package com.rumahtaqwa.ui.screens.auth.verify

data class VerifyEmailState(
    val email: String = "",
    val isChecking: Boolean = false,
    val isResending: Boolean = false,
    val isVerified: Boolean = false,
    val message: String? = null,
    val isMessageError: Boolean = false
)
