package com.rumahtaqwa.domain.usecase

import com.rumahtaqwa.data.model.Ibadah
import com.rumahtaqwa.data.model.IbadahSetting
import com.rumahtaqwa.domain.repository.IbadahRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IbadahUseCase @Inject constructor(
    private val repository: IbadahRepository
) {
    fun getIbadah(): Flow<List<Ibadah>> = repository.getIbadah()

    fun getIbadahSettings(): Flow<Map<String, IbadahSetting>?> = repository.getIbadahSettings()

    fun getListenerLogs(startDate: String, endDate: String): Flow<List<Map<String, Any>>?> = repository.getListenerLogs(startDate, endDate)

    suspend fun getLogs(startDate: String, endDate: String): List<Map<String, Any>>? = repository.getLogs(startDate, endDate)

    suspend fun updateSettings(settings: Map<String, IbadahSetting>) = repository.updateSettings(settings)

    suspend fun saveIbadahByDate(date: String, field: String, value: String) = repository.saveIbadahByDate(date, field, value)

    suspend fun saveLogsForMonth(
        data: Map<String, Map<String, String>>,
        previousData: Map<String, Map<String, String>>
    ) = repository.saveLogsForMonth(data, previousData)
}