package com.rumahtaqwa.ui.screens.main.ibadah

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumahtaqwa.core.util.SnackbarController
import com.rumahtaqwa.data.model.IbadahSetting
import com.rumahtaqwa.domain.usecase.IbadahUseCase
import com.rumahtaqwa.domain.usecase.ReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IbadahViewModel @Inject constructor(
    private val ibadahUseCase: IbadahUseCase,
    private val reminderUseCase: ReminderUseCase,
    private val snackbarController: SnackbarController
) : ViewModel() {
    private val _state = MutableStateFlow(IbadahState())
    val state = _state.asStateFlow()

    init {
        observerIbadah()
        observerIbadahSettings()
        observerReminders()
    }

    private fun observerIbadah() {
        viewModelScope.launch {
            ibadahUseCase.getIbadah()
                .catch { e ->
                    snackbarController.showError(e.message ?: "Gagal memuat data ibadah")
                    _state.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { ibadah ->
                    _state.update { it.copy(ibadah = ibadah, isLoading = false) }
                }
        }
    }

    private fun observerIbadahSettings() {
        viewModelScope.launch {
            ibadahUseCase.getIbadahSettings()
                .catch { e ->
                    snackbarController.showError(e.message ?: "Gagal memuat pengaturan ibadah")
                    _state.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { settings ->
                    val updatedSettings = settings?.toMutableMap() ?: mutableMapOf()
                    val listIbadah = _state.value.ibadah
                    listIbadah.forEach { ibadah ->
                        if (updatedSettings[ibadah.id] == null) {
                            updatedSettings[ibadah.id] = IbadahSetting(
                                id = ibadah.id,
                                field = ibadah.field,
                                recap = true,
                                perWeek = 1,
//                                notify = false,
//                                notifTime = ibadah.defaultTime
                            )
                        }
                    }
                    _state.update { it.copy(settings = updatedSettings, isLoading = false) }
                }
        }
    }

    private fun observerReminders() {
        viewModelScope.launch {
            reminderUseCase.observeAllReminders()
                .catch { e ->
                    snackbarController.showError(e.message ?: "Gagal memuat pengingat")
                    _state.update { it.copy(error = e.message) }
                }
                .collect { reminders ->
                    val map = reminders.associateBy { it.ibadahId }
                    _state.update { it.copy(reminders = map) }
                }
        }
    }

    fun onChangeSettings(field: IbadahField, ibadahId: String, value: String) {
        val updatedSettings = _state.value.settings?.toMutableMap() ?: mutableMapOf()
        updatedSettings[ibadahId]?.let { current ->
            updatedSettings[ibadahId] = when (field) {
                IbadahField.PERWEEK -> current.copy(perWeek = value.toInt())
                IbadahField.ISRECAP -> current.copy(recap = value.toBoolean())
            }
//            val current = updatedSettings[ibadahId]?.copy()
//            updatedSettings[ibadahId] = updateCurSetting(current, field, value)
        }
        _state.update {
            it.copy(
                settings = updatedSettings
            )
        }
    }

    fun updateSettings() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val settings = _state.value.settings ?: return@launch
            try {
                ibadahUseCase.updateSettings(settings)
                snackbarController.showSuccess("Pengaturan berhasil diterapkan")
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                snackbarController.showError(e.message ?: "Gagal menerapkan pengaturan")
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onToggleReminder(ibadahId: String, label: String, enabled: Boolean) {
        viewModelScope.launch {
            reminderUseCase.toggleReminder(ibadahId, label, enabled)
        }
    }

    fun onChangeReminderTime(ibadahId: String, time: String) {
        viewModelScope.launch {
            reminderUseCase.updateTime(ibadahId, time)
        }
    }

    fun onScreenResumed() {
        viewModelScope.launch {
            reminderUseCase.retryPendingSchedules()
        }
    }
}