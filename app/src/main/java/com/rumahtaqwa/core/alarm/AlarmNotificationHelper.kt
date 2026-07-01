package com.rumahtaqwa.core.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.rumahtaqwa.data.local.reminder.IbadahReminderEntity

object AlarmNotificationHelper {
    const val CHANNEL_ID = "alarm_channel"
    const val EXTRA_LABEL = "extra_label"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID, "Alarm Pengingat Ibadah", NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setBypassDnd(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(null, null) // suara diputar manual via AlarmSoundPlayer, bukan channel
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    fun showFullScreenAlarm(context: Context, ibadahId: String, reminder: IbadahReminderEntity, globalSoundEnabled: Boolean = true) {
        val requestCode = ibadahId.hashCode()
        val label = reminder.label.ifEmpty { ibadahId }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            Intent(context, AlarmRingActivity::class.java).apply {
                putExtra(AlarmScheduler.EXTRA_IBADAH_ID, ibadahId)
                putExtra(EXTRA_LABEL, label)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, DismissReceiver::class.java).apply {
                putExtra(AlarmScheduler.EXTRA_IBADAH_ID, ibadahId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, SnoozeReceiver::class.java).apply {
                putExtra(AlarmScheduler.EXTRA_IBADAH_ID, ibadahId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val canUseFullScreen = Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE ||
                context.getSystemService(NotificationManager::class.java).canUseFullScreenIntent()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(com.rumahtaqwa.R.drawable.ic_alarm)
            .setColor(Color.parseColor("#F59E0B"))
            .setContentTitle("Waktunya $label")
            .setContentText("Ketuk untuk membuka pengingat")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .addAction(0, "Selesai", dismissPendingIntent)
            .addAction(0, "Tunda", snoozePendingIntent)
            .apply { if (canUseFullScreen) setFullScreenIntent(fullScreenPendingIntent, true) }
            .build()

        context.getSystemService(NotificationManager::class.java).notify(requestCode, notification)

        if (reminder.soundEnabled && globalSoundEnabled) AlarmSoundPlayer.play(context, reminder.soundUri)
        if (reminder.vibrate) {
            context.getSystemService(Vibrator::class.java)
                ?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 800, 500), 0))
        }
    }

    fun showFallbackNotification(context: Context, ibadahId: String) {
        runCatching {
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(com.rumahtaqwa.R.drawable.ic_alarm)
                .setContentTitle("Alarm Pengingat")
                .setContentText("Ketuk untuk membuka aplikasi")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .build()
            context.getSystemService(NotificationManager::class.java)
                .notify(ibadahId.hashCode(), notification)
        }
    }

    fun dismiss(context: Context, ibadahId: String) {
        context.getSystemService(NotificationManager::class.java).cancel(ibadahId.hashCode())
        AlarmSoundPlayer.stop()
        context.getSystemService(Vibrator::class.java)?.cancel()
    }
}