package com.rumahtaqwa.domain.repository

import com.rumahtaqwa.data.local.reminder.IbadahReminderEntity
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    suspend fun getReminder(ibadahId: String): IbadahReminderEntity?
    fun observeReminder(ibadahId: String): Flow<IbadahReminderEntity?>
    fun observeAllReminders(): Flow<List<IbadahReminderEntity>>
    suspend fun getAllEnabled(): List<IbadahReminderEntity>
    suspend fun setNotifyEnabled(ibadahId: String, label: String, enabled: Boolean)
    suspend fun setNotifyTime(ibadahId: String, time: String)
    suspend fun setSoundConfig(ibadahId: String, enabled: Boolean, uri: String?, vibrate: Boolean, snooze: Int)
}