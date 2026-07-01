package com.rumahtaqwa.domain.usecase

import com.rumahtaqwa.core.alarm.AlarmScheduler
import com.rumahtaqwa.domain.repository.ReminderRepository
import javax.inject.Inject

class ReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val alarmScheduler: AlarmScheduler
) {
    fun observeAllReminders() = reminderRepository.observeAllReminders()

    fun observeReminder(ibadahId: String) = reminderRepository.observeReminder(ibadahId)

    suspend fun toggleReminder(ibadahId: String, label: String, enabled: Boolean) {
        reminderRepository.setNotifyEnabled(ibadahId, label, enabled)
        rescheduleOrCancel(ibadahId)
    }

    suspend fun updateTime(ibadahId: String, time: String) {
        reminderRepository.setNotifyTime(ibadahId, time)
        rescheduleOrCancel(ibadahId)
    }

    suspend fun updateSoundConfig(ibadahId: String, enabled: Boolean, uri: String?, vibrate: Boolean, snooze: Int) {
        reminderRepository.setSoundConfig(ibadahId, enabled, uri, vibrate, snooze)
        rescheduleOrCancel(ibadahId) // reschedule perlu supaya AlarmClockInfo showIntent ke-refresh
    }

    suspend fun rescheduleAllOnBoot() {
        reminderRepository.getAllEnabled().forEach { alarmScheduler.schedule(it) }
    }

    suspend fun retryPendingSchedules() {
        if (!alarmScheduler.canScheduleExactAlarms()) return
        reminderRepository.getAllEnabled().forEach { reminder ->
            alarmScheduler.schedule(reminder)
        }
    }

    private suspend fun rescheduleOrCancel(ibadahId: String) {
        val entity = reminderRepository.getReminder(ibadahId) ?: return
        if (entity.notify) alarmScheduler.schedule(entity) else alarmScheduler.cancel(ibadahId)
    }
}