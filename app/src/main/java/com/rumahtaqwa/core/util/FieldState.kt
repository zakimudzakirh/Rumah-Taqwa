package com.rumahtaqwa.core.util

sealed class FieldState {
    object Idle : FieldState()
    object Valid : FieldState()
    data class Error(val message: String) : FieldState()
}