package com.rumahtaqwa.domain.repository

import com.rumahtaqwa.data.model.Ibadah
import com.rumahtaqwa.data.model.IbadahSetting
import kotlinx.coroutines.flow.Flow

interface IbadahRepository {
    fun getIbadah(): Flow<List<Ibadah>>
    fun getIbadahSettings(): Flow<Map<String, IbadahSetting>?>
    suspend fun getLogs(startDate: String, endDate: String): List<Map<String, Any>>?
    fun getListenerLogs(startDate: String, endDate: String): Flow<List<Map<String, Any>>?>
    suspend fun updateSettings(settings: Map<String, IbadahSetting>)
    suspend fun saveIbadahByDate(date: String, field: String, value: String)
    suspend fun saveLogsForMonth(
        data: Map<String, Map<String, String>>,
        previousData: Map<String, Map<String, String>>
    )
}