package com.rumahtaqwa.core.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class SnackbarType {
    SUCCESS, WARNING, ERROR
}

data class SnackbarMessage(
    val message: String,
    val type: SnackbarType
)

/**
 * Singleton event bus untuk snackbar global di area main (Home/Ibadah/Rekap/Settings).
 * Inject langsung ke ViewModel mana pun yang perlu menampilkan feedback.
 */
@Singleton
class SnackbarController @Inject constructor() {
    private val _messages = MutableSharedFlow<SnackbarMessage>(extraBufferCapacity = 1)
    val messages = _messages.asSharedFlow()

    fun showSuccess(message: String) = show(message, SnackbarType.SUCCESS)

    fun showWarning(message: String) = show(message, SnackbarType.WARNING)

    fun showError(message: String) = show(message, SnackbarType.ERROR)

    private fun show(message: String, type: SnackbarType) {
        _messages.tryEmit(SnackbarMessage(message, type))
    }
}
