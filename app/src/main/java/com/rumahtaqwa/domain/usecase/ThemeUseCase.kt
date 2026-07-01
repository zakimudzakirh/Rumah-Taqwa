package com.rumahtaqwa.domain.usecase

import com.rumahtaqwa.core.datastore.ThemePreferenceManager
import com.rumahtaqwa.core.util.ThemeMode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ThemeUseCase @Inject constructor(
    private val themePreferenceManager: ThemePreferenceManager
) {
    fun getThemeMode(): Flow<ThemeMode> = themePreferenceManager.themeMode

    suspend fun setThemeMode(mode: ThemeMode) {
        themePreferenceManager.setThemeMode(mode)
    }
}