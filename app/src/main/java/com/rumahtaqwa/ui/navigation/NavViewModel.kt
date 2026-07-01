package com.rumahtaqwa.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumahtaqwa.core.util.ThemeMode
import com.rumahtaqwa.domain.usecase.AuthUseCase
import com.rumahtaqwa.domain.usecase.ThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val themeUseCase: ThemeUseCase
) : ViewModel() {
    val isLoggedIn = authUseCase.isLoggedIn

    val themeMode = themeUseCase.getThemeMode()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themeUseCase.setThemeMode(mode)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authUseCase.logout()
        }
    }
}