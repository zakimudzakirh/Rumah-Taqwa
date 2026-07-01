package com.rumahtaqwa.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import com.rumahtaqwa.core.datastore.SettingsPreferenceManager
import com.rumahtaqwa.domain.repository.ReminderRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject lateinit var reminderRepository: ReminderRepository
    @Inject lateinit var alarmScheduler: AlarmScheduler
    @Inject lateinit var settingsPreferenceManager: SettingsPreferenceManager

    override fun onReceive(context: Context, intent: Intent) {
        val ibadahId = intent.getStringExtra(AlarmScheduler.EXTRA_IBADAH_ID) ?: return
        val isSnooze = intent.getBooleanExtra(AlarmScheduler.EXTRA_IS_SNOOZE, false)

        val wakeLock = (context.getSystemService(Context.POWER_SERVICE) as PowerManager)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "RumahTaqwa:AlarmWakeLock:$ibadahId")
        wakeLock.acquire(10_000L)

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reminder = reminderRepository.getReminder(ibadahId)
                val globalSound = settingsPreferenceManager.soundEnabled.first()
                if (reminder != null && reminder.notify) {
                    AlarmNotificationHelper.showFullScreenAlarm(context, ibadahId, reminder, globalSound)
                    if (!isSnooze) alarmScheduler.schedule(reminder)
                }
            } catch (e: Exception) {
                Log.e("AlarmReceiver", "Gagal menampilkan alarm $ibadahId", e)
                AlarmNotificationHelper.showFallbackNotification(context, ibadahId)
            } finally {
                wakeLock.release()
                pendingResult.finish()
            }
        }
    }
}