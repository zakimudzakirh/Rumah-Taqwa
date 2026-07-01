package com.rumahtaqwa.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SnoozeReceiver : BroadcastReceiver() {
    @Inject lateinit var alarmScheduler: AlarmScheduler
    @Inject lateinit var reminderRepository: com.rumahtaqwa.domain.repository.ReminderRepository

    override fun onReceive(context: Context, intent: Intent) {
        val ibadahId = intent.getStringExtra(AlarmScheduler.EXTRA_IBADAH_ID) ?: return
        AlarmNotificationHelper.dismiss(context, ibadahId)

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            reminderRepository.getReminder(ibadahId)?.let {
                alarmScheduler.scheduleSnooze(ibadahId, it.snoozeMinutes)
            }
            pendingResult.finish()
        }
    }
}