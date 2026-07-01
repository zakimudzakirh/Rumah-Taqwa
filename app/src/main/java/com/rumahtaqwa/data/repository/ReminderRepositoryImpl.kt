package com.rumahtaqwa.data.repository

import com.rumahtaqwa.data.local.reminder.IbadahReminderDao
import com.rumahtaqwa.data.local.reminder.IbadahReminderEntity
import com.rumahtaqwa.domain.repository.ReminderRepository
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val dao: IbadahReminderDao
) : ReminderRepository {

    override suspend fun getReminder(ibadahId: String) = dao.getById(ibadahId)
    override fun observeReminder(ibadahId: String) = dao.observeById(ibadahId)
    override fun observeAllReminders() = dao.observeAll()
    override suspend fun getAllEnabled() = dao.getAllEnabled()

    override suspend fun setNotifyEnabled(ibadahId: String, label: String, enabled: Boolean) {
        val current = dao.getById(ibadahId) ?: IbadahReminderEntity(ibadahId = ibadahId)
        dao.upsert(current.copy(label = label, notify = enabled))
    }

    override suspend fun setNotifyTime(ibadahId: String, time: String) {
        val current = dao.getById(ibadahId) ?: IbadahReminderEntity(ibadahId = ibadahId)
        dao.upsert(current.copy(notifTime = time))
    }

    override suspend fun setSoundConfig(ibadahId: String, enabled: Boolean, uri: String?, vibrate: Boolean, snooze: Int) {
        val current = dao.getById(ibadahId) ?: IbadahReminderEntity(ibadahId = ibadahId)
        dao.upsert(current.copy(soundEnabled = enabled, soundUri = uri, vibrate = vibrate, snoozeMinutes = snooze))
    }
}