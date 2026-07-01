package com.rumahtaqwa.ui.screens.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumahtaqwa.core.util.ThemeMode
import com.rumahtaqwa.core.util.toDayNumber
import com.rumahtaqwa.core.util.toFormatDataString
import com.rumahtaqwa.data.model.IbadahSetting
import com.rumahtaqwa.domain.usecase.AuthUseCase
import com.rumahtaqwa.domain.usecase.IbadahUseCase
import com.rumahtaqwa.domain.usecase.QuranUseCase
import com.rumahtaqwa.domain.usecase.ThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val ibadahUseCase: IbadahUseCase,
    private val quranUseCase: QuranUseCase,
    private val themeUseCase: ThemeUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                user = authUseCase.currentUser
            )
        }
        observeSurats()
        observerIbadah()
        observerIbadahSettings()
    }

    private fun observerIbadah() {
        viewModelScope.launch {
            ibadahUseCase.getIbadah()
                .catch { e -> _state.update { it.copy(error = e.message) } }
                .collect { ibadah ->
                    _state.update { it.copy(ibadah = ibadah, isLoading = false) }
                }
        }
    }

    private fun observeSurats() {
        viewModelScope.launch {
            quranUseCase.getAllSurat()
                .catch { e ->
                    _state.update { it.copy(error = e.message) }
                }
                .collect { surat ->
                    _state.update {
                        it.copy(
                            surats = surat
                        )
                    }
                }
        }
    }

    private fun observerIbadahSettings() {
        viewModelScope.launch {
            ibadahUseCase.getIbadahSettings()
                .catch { e ->
                    _state.update { it.copy(error = e.message) }
                }
                .collect { settings ->
                    val updatedSettings = settings?.toMutableMap() ?: mutableMapOf()
                    val listIbadah = _state.value.ibadah
                    var total = 0
                    listIbadah.forEach { ibadah ->
                        if (updatedSettings[ibadah.id] == null) {
                            updatedSettings[ibadah.id] = IbadahSetting(
                                id = ibadah.id,
                                field = ibadah.field,
                                recap = true,
                                perWeek = 1
                            )
                        }
                        if (updatedSettings[ibadah.id]?.recap == true) {
                            total += (updatedSettings[ibadah.id]?.perWeek ?: 0)
                        }
                    }
                    _state.update {
                        it.copy(
                            settings = updatedSettings,
                            isLoading = false,
                            weeklyTotal = total
                        )
                    }
                }
        }
    }

    fun getCurrentWeekDays(): List<Date> {
        val calendar = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return (0..6).map { dayOffset ->
            calendar.time.also { calendar.add(Calendar.DAY_OF_MONTH, 1) }
        }
    }

    fun fetchWeekly() {
        viewModelScope.launch {
            val week = getCurrentWeekDays()
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDate = week[0].toFormatDataString()
            val endDate = week[week.size - 1].toFormatDataString()
            val logs = ibadahUseCase.getLogs(
                startDate, endDate
            )
            val curDate = formatter.format(Date())
            val todayLog = logs?.firstOrNull { log ->
                log["date"] == curDate
            }
            val today = _state.value.ibadah.associate { ibadah ->
                ibadah.id to (todayLog?.get(ibadah.id)?.toString().orEmpty())
            }
            val weekly = _state.value.ibadah.associate { ibadah ->
                ibadah.id to (
                    logs?.count { log ->
                        log[ibadah.id]?.toString()?.isNotBlank() == true
                    } ?: 0
                )
            }
            var count = 0
            val settings = _state.value.settings
            for (ibadah in _state.value.ibadah) {
                if (settings?.get(ibadah.id)?.recap == true) {
                    count += (weekly[ibadah.id] ?: 0)
                }
            }
            val weeklyTotal = _state.value.weeklyTotal
            _state.update {
                it.copy(
                    weekly = weekly,
                    today = today,
                    weeklyCount = count,
                    weeklyProgress = count.toFloat() / weeklyTotal.toFloat()
                )
            }
        }
    }

    fun updateData(ibadahId: String, value: String) {
        val date = Date().toFormatDataString()
        viewModelScope.launch {
            ibadahUseCase.saveIbadahByDate(date, ibadahId, value)
            fetchWeekly()
        }
    }

    val themeMode = themeUseCase.getThemeMode()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.DARK
        )

    fun setTheme(themeMode: ThemeMode) {
        viewModelScope.launch {
            themeUseCase.setThemeMode(themeMode)
        }
    }

}