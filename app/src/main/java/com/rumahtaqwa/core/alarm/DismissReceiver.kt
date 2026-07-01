package com.rumahtaqwa.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val ibadahId = intent.getStringExtra(AlarmScheduler.EXTRA_IBADAH_ID) ?: return
        AlarmNotificationHelper.dismiss(context, ibadahId)
    }
}