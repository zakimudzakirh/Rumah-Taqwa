package com.rumahtaqwa.ui.screens.main.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumahtaqwa.core.datastore.SettingsPreferenceManager
import com.rumahtaqwa.core.util.ThemeMode
import com.rumahtaqwa.domain.usecase.AuthUseCase
import com.rumahtaqwa.domain.usecase.ThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val themeUseCase: ThemeUseCase,
    private val settingsPreferenceManager: SettingsPreferenceManager,
) : ViewModel() {

    val user = authUseCase.currentUser

    val themeMode = themeUseCase.getThemeMode()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )

    val soundEnabled = settingsPreferenceManager.soundEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun setTheme(themeMode: ThemeMode) {
        viewModelScope.launch {
            themeUseCase.setThemeMode(themeMode)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsPreferenceManager.setSoundEnabled(enabled)
        }
    }
}
