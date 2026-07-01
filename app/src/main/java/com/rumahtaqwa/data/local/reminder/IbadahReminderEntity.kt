package com.rumahtaqwa.data.local.reminder

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ibadah_reminder")
data class IbadahReminderEntity(
    @PrimaryKey val ibadahId: String,
    val label: String = "",
    val notify: Boolean = false,
    val notifTime: String = "00:00",
    val soundEnabled: Boolean = true,
    val soundUri: String? = null,
    val vibrate: Boolean = true,
    val snoozeMinutes: Int = 5
)