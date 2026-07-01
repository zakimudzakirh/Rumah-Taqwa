package com.rumahtaqwa.core.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.rumahtaqwa.MainActivity
import com.rumahtaqwa.data.local.reminder.IbadahReminderEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import androidx.core.net.toUri

class AlarmScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(reminder: IbadahReminderEntity) {
        if (!reminder.notify) { cancel(reminder.ibadahId); return }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            requestExactAlarmPermission()
            return
        }

        val (hour, minute) = parseTime(reminder.notifTime)
        val triggerTime = nextTriggerTimeMillis(hour, minute)
        val requestCode = reminder.ibadahId.hashCode()

        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_IBADAH_ID, reminder.ibadahId)
        }
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context, requestCode, alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val showIntent = PendingIntent.getActivity(
            context, requestCode, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(triggerTime, showIntent), alarmPendingIntent)
    }

    fun scheduleSnooze(ibadahId: String, snoozeMinutes: Int) {
        val triggerTime = System.currentTimeMillis() + snoozeMinutes * 60_000L
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_IBADAH_ID, ibadahId)
            putExtra(EXTRA_IS_SNOOZE, true)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, ibadahId.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(triggerTime, null), pendingIntent)
    }

    fun cancel(ibadahId: String) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, ibadahId.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.startActivity(
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    .setData("package:${context.packageName}".toUri())
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    private fun parseTime(time: String): Pair<Int, Int> {
        val (h, m) = time.split(":").map { it.toInt() }
        return h to m
    }

    private fun nextTriggerTimeMillis(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val candidate = now.clone() as Calendar
        candidate.set(Calendar.HOUR_OF_DAY, hour)
        candidate.set(Calendar.MINUTE, minute)
        candidate.set(Calendar.SECOND, 0)
        if (candidate.timeInMillis <= now.timeInMillis) {
            candidate.add(Calendar.DAY_OF_YEAR, 1)
        }
        return candidate.timeInMillis
    }

    companion object {
        const val EXTRA_IBADAH_ID = "extra_ibadah_id"
        const val EXTRA_IS_SNOOZE = "extra_is_snooze"
    }
}